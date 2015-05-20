package dk.bayes.infer.gp.sgpr

import dk.bayes.math.linear.Matrix
import dk.bayes.infer.gp.cov.CovFunc
import dk.bayes.infer.gp.mean.MeanFunc
import dk.bayes.infer.gp.mean.ZeroMean
import scala.math._

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

  private val noiseStdDev = exp(noiseLogStdDev)

  private val kUU = covFunc.cov(u) + Matrix.identity(u.numRows()) * 1e-7 //add some jitter
  private val kUX = covFunc.covNM(u, x)
  private val kXU = kUX.t

  private val sigma = (kUU + pow(noiseStdDev, -2) * kUX * kXU).inv
  private val kUUinv = kUU.inv

  def predict(z: Matrix): Matrix = {

    val kZZ = covFunc.cov(z)
    val kZU = covFunc.covNM(z, u)
    val kUZ = kZU.t

    //@TODO use Cholesky Factorization instead of a direct inverse
    val predMean = pow(noiseStdDev, -2) * kZU * sigma * kUX * y
    val predVariance = kZZ - kZU * kUUinv * kUZ + kZU * sigma * kUZ

    predMean.combine(0, 1, predVariance.extractDiag)
  }

}