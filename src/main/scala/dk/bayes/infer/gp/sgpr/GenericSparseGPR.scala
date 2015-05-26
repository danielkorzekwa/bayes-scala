package dk.bayes.infer.gp.sgpr

import dk.bayes.math.linear.Matrix
import dk.bayes.infer.gp.cov.CovFunc
import dk.bayes.infer.gp.mean.MeanFunc
import dk.bayes.infer.gp.mean.ZeroMean
import scala.math._
import breeze.linalg.logdet
import breeze.linalg.DenseMatrix
import breeze.linalg.trace
import scala.language.implicitConversions
import breeze.linalg.DenseVector
import breeze.linalg.inv
import breeze.linalg.diag

/**
 * Gaussian Process Regression. It uses Gaussian likelihood and zero mean functions.
 * Based on: Variational Learning of Inducing Variables in Sparse Gaussian Processes (http://jmlr.org/proceedings/papers/v5/titsias09a/titsias09a.pdf)
 *
 * @param x Inputs. [NxD] matrix, where N - number of training examples, D - dimensionality of input space
 * @param y Targets. [Nx1] matrix, where N - number of training examples
 * @param u Inducing points. [NxD] matrix, where N - number of inducing points, D - dimensionality of input space
 * @param covFunc Covariance function
 * @param noiseLogStdDev Log of noise standard deviation of Gaussian likelihood function
 *
 */
case class GenericSparseGPR(x: Matrix, y: Matrix, u: Matrix, covFunc: CovFunc, noiseLogStdDev: Double) extends SparseGPR {

  private val likNoiseStdDev = exp(noiseLogStdDev)
  private val likNoiseVar = likNoiseStdDev * likNoiseStdDev

  private val kMM: DenseMatrix[Double] = covFunc.cov(u) + Matrix.identity(u.numRows()) * 1e-7 //add some jitter
  private val kMN: DenseMatrix[Double] = covFunc.covNM(u, x)
  private val kNM = kMN.t
  private val kNNdiag = Matrix((0 until x.numRows()).map(rowIndex => covFunc.cov(x.extractRow(rowIndex)).at(0) + 1e-7).toArray)
  private val sigma = inv(kMM + pow(likNoiseStdDev, -2) * kMN * kNM)
  private val kMMinv = inv(kMM)

  private val yValue: DenseVector[Double] = y

  def predict(z: Matrix): Matrix = {

    val kZZ: DenseMatrix[Double] = covFunc.cov(z)
    val kZU: DenseMatrix[Double] = covFunc.covNM(z, u)
    val kUZ = kZU.t

    //@TODO use Cholesky Factorization instead of a direct inverse
    val predMean = pow(likNoiseStdDev, -2) * kZU * sigma * kMN * yValue
    val predVariance = kZZ - kZU * kMMinv * kUZ + kZU * sigma * kUZ

    val a = diag(predVariance)
    Matrix(predMean.toArray).combine(0, 1, Matrix(diag(predVariance).toArray))
  }

  /**
   * Returns Tuple3(
   * the value of lower bound,
   * derivatives of variational lower bound with respect to covariance hyper parameters,
   * derivatives of variational lower bound with respect to likelihood log noise std dev
   * )
   */
  def loglikWithD(kMMdArray: Array[DenseMatrix[Double]], kNMdArray: Array[DenseMatrix[Double]], kNNDiagDArray: Array[DenseVector[Double]]): Tuple3[Double, Array[Double], Double] = {

    val n = x.numRows()
    val m = u.numRows()

    calcLowerBoundWithD(kMM, kMMinv, kMMdArray, kMN, kNM, kNMdArray, kNNdiag, kNNDiagDArray, y, likNoiseVar, n, m)
  }

  private implicit def toDenseMatrix(m: Matrix): DenseMatrix[Double] = {
    DenseMatrix(m.t.toArray).reshape(m.numRows, m.numCols)
  }

  private implicit def toDenseVector(m: Matrix): DenseVector[Double] = {
    DenseVector(m.toArray)
  }

}