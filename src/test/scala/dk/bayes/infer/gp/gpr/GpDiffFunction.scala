package dk.bayes.infer.gp.gpr

import breeze.optimize.DiffFunction
import breeze.linalg.DenseVector
import dk.bayes.math.linear._
import breeze.linalg._
import dk.bayes.infer.gp.cov.CovSEiso
import scala.math._
import dk.bayes.math.linear.Matrix

case class GpDiffFunction(x: Matrix, y: Matrix) extends DiffFunction[DenseVector[Double]] {

  /**
   * @param x Logarithm of [signal standard deviation,length-scale,likelihood noise standard deviation]
   */
  def calculate(params: DenseVector[Double]): (Double, DenseVector[Double]) = {
    val (sf, ell, likStdDev) = (params(0), params(1), params(2))

    val covFunc = CovSEiso(sf, ell)

    val gp = GenericGPRegression(x, y, covFunc, likStdDev)
    val f = -gp.loglik()

    //calculate partial derivatives
    val df_sf = -gp.loglikD(covFunc.df_dSf(x))
    val df_ell = -gp.loglikD(covFunc.df_dEll(x))
    val df_likStdDev = -gp.loglikD(2 * exp(2 * likStdDev) * Matrix.identity(x.numRows))

    (f, DenseVector(df_sf, df_ell, df_likStdDev))
  }

}