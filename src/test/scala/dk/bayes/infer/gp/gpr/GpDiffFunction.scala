package dk.bayes.infer.gp.gpr

import breeze.linalg._
import breeze.linalg.DenseVector
import breeze.optimize.DiffFunction
import dk.bayes.infer.gp.cov.CovSEiso
import breeze.numerics._

case class GpDiffFunction(x: DenseMatrix[Double], y: DenseVector[Double]) extends DiffFunction[DenseVector[Double]] {

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
    val df_likStdDev = -gp.loglikD(2 * exp(2 * likStdDev) * DenseMatrix.eye[Double](x.rows))

    (f, DenseVector(df_sf, df_ell, df_likStdDev))
  }

}