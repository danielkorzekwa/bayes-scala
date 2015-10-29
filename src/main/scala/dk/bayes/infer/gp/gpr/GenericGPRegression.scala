package dk.bayes.infer.gp.gpr

import scala.language.implicitConversions
import scala.math._
import breeze.linalg.DenseMatrix
import breeze.linalg.logdet
import dk.bayes.infer.gp.cov.CovFunc
import dk.bayes.infer.gp.mean.MeanFunc
import dk.bayes.infer.gp.mean.ZeroMean
import breeze.linalg.DenseVector
import breeze.linalg.trace
import breeze.linalg.inv
import dk.bayes.math.linear.createDenseMatrixElemWise
import breeze.linalg.diag
import breeze.linalg.cholesky
import dk.bayes.math.linear.invchol
import dk.bayes.math.linear.logdetchol

/**
 * Gaussian Process Regression. It uses Gaussian likelihood and zero mean functions.
 *
 * @param x Inputs. [NxD] matrix, where N - number of training examples, D - dimensionality of input space
 * @param y Targets. [Nx1] matrix, where N - number of training examples
 * @param covFunc Covariance function
 * @param noiseStdDev Log of noise standard deviation of Gaussian likelihood function
 *
 */
case class GenericGPRegression(x: DenseMatrix[Double], y: DenseVector[Double], covFunc: CovFunc, noiseStdDev: Double, meanFunc: MeanFunc = ZeroMean()) extends GPRegression {

  private val kXX = cov(x)
  private val kXXInv = invchol(cholesky(kXX).t)

  private val meanX = meanFunc.mean(x)

  /**
   * @param z Inputs for making predictions. [NxD] matrix. N - number of test points, D - dimensionality of input space
   * @return Predicted targets.[mean variance]
   */
  def predict(z: DenseMatrix[Double]): DenseMatrix[Double] = {

    val kXZ = cov(x, z)
    val kZZ = cov(z)

    val meanZ = meanFunc.mean(z)

    //@TODO use Cholesky Factorization instead of a direct inverse
    val predMean = meanZ + kXZ.t * (kXXInv * (y - meanX))
    val predVar = kZZ - kXZ.t * kXXInv * kXZ

    DenseMatrix.horzcat(predMean.toDenseMatrix.t,diag(predVar).toDenseMatrix.t)
  }

  //e.q. 5.8 from page 114, Carl Edward Rasmussen and Christopher K. I. Williams, The MIT Press, 2006
  def loglik(): Double = {

    val m = meanX
    val logDet = logdetchol(cholesky(kXX))
    val loglikValue = -0.5 * ((y - m).t * kXXInv * (y - m)) - 0.5 * logDet - 0.5 * x.rows.toDouble * log(2 * Pi)
    loglikValue
  }

  //e.q. 5.9 from page 114, Carl Edward Rasmussen and Christopher K. I. Williams, The MIT Press, 2006
  def loglikD(covElemWiseD:  DenseMatrix[Double]): Double = {
    val m = meanX
    val d: DenseMatrix[Double] = covElemWiseD
    val a = kXXInv * (y - m)
    0.5 * trace((a * a.t - kXXInv) * d)
  }

  /**
   * @param v [N x D] vector
   * @return [N x N] covariance matrix
   */
  private def cov(v: DenseMatrix[Double]):  DenseMatrix[Double] = covFunc.cov(v) + exp(2 * noiseStdDev) * DenseMatrix.eye[Double](v.rows)

  /**
   * @param x [N x D] vector
   * @param z [M x D] vector
   * @return [N x M] covariance matrix
   */
  private def cov(x:  DenseMatrix[Double], z:  DenseMatrix[Double]):  DenseMatrix[Double] = {
    createDenseMatrixElemWise(x.rows, z.rows, (rowIndex, colIndex) => covFunc.cov(x(rowIndex,::).t.toArray, z(colIndex,::).t.toArray))
  }

}