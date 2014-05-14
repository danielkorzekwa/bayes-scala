package dk.bayes.learn.gp

import breeze.optimize.DiffFunction
import breeze.linalg.DenseVector
import dk.bayes.math.linear._
import breeze.linalg._
import dk.bayes.infer.gp.CovFunc
import dk.bayes.infer.gp.CovSEiso
import scala.math._
import dk.bayes.math.linear.Matrix

case class GpDiffFunction(x: Matrix, y: Matrix) extends DiffFunction[DenseVector[Double]] {

  /**
   * @param x Logarithm of [length-scale,signal standard deviation,likelihood noise standard deviation]
   */
  def calculate(params: DenseVector[Double]): (Double, DenseVector[Double]) = {
    val (sf, ell, likStdDev) = (params(0), params(1), params(2))

    val covFunc = CovSEiso(sf, ell)
    val cov = covFunc.cov(x) + exp(2 * likStdDev) * Matrix.identity(x.numRows)
    val covInv = cov.inv

    val f = loglik(cov, covInv)

    //calculate partial derivatives
    val df_sf = loglikD(covInv, covFunc.df_dSf(x))
    val df_ell = loglikD(covInv, covFunc.df_dEll(x))
    val df_likStdDev = loglikD(covInv, 2 * exp(2 * likStdDev) * Matrix.identity(x.numRows))

    (f, DenseVector(df_sf, df_ell, df_likStdDev))
  }

  //e.q. 5.8 from page 114, Carl Edward Rasmussen and Christopher K. I. Williams, The MIT Press, 2006
  private def loglik(cov: Matrix, covInv: Matrix): Double = {
    -(-0.5 * y.t * covInv * y - 0.5 * log(cov.det) - 0.5 * x.numRows.toDouble * log(2 * Pi)).at(0)
  }

  //e.q. 5.9 from page 114, Carl Edward Rasmussen and Christopher K. I. Williams, The MIT Press, 2006
  private def loglikD(covInv: Matrix, covElemWiseD: DenseMatrix[Double]): Double = {
    val a = covInv * y
    -0.5 * trace((a * a.t - covInv) * covElemWiseD)
  }

  private implicit def toDenseMatrix(m: Matrix): DenseMatrix[Double] = {
    DenseMatrix(m.toArray).reshape(m.numRows, m.numCols)
  }
}