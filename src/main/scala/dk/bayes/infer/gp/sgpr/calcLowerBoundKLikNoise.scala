package dk.bayes.infer.gp.sgpr

import breeze.linalg.DenseMatrix
import breeze.linalg.trace
import breeze.linalg.DenseVector
import breeze.linalg.inv
import breeze.linalg.sum

/**
 * Calculates derivative of variational lower bound with respect to likelihood log noise std dev for Sparse GP regression model.
 *
 * Based on:
 * - Variational Learning of Inducing Variables in Sparse Gaussian Processes (http://jmlr.org/proceedings/papers/v5/titsias09a/titsias09a.pdf)
 * - Derivatives of lower bound, Michalis K. Titsias
 */
object calcLowerBoundKLikNoise {

  /**
   * @param lowerBoundTerms 6 terms of lower bound
   *   @param a likNoiseVar*kMM + kMN*kNM
   */
  def apply(kMM: DenseMatrix[Double],kMMinv: DenseMatrix[Double],  kMN: DenseMatrix[Double], kNM: DenseMatrix[Double], kNNdiag: DenseVector[Double],
      y: DenseVector[Double], likNoiseVar: Double, n: Double, m: Double,
      lowerBoundTerms:Array[Double], a:DenseMatrix[Double]): Double = {
   val f0Term = f0D(y, likNoiseVar, n, m)
   val f2Term = f2D(kMM, kMN, kNM, likNoiseVar,a) 
   val f3Term = f3D(kMM, kMN, kNM, y, likNoiseVar,a) 
   val f4Term =  f4D(lowerBoundTerms(4)) 
   val f5Term = f5D(lowerBoundTerms(5))
   
   f0Term + f2Term + f3Term + f4Term + f5Term
  }

  private def f0D(y: DenseVector[Double], likNoiseVar: Double, n: Double, m: Double): Double = -(n - m) + ((1d / likNoiseVar) * (y.t * y))

  private def f2D(kMM: DenseMatrix[Double], kMN: DenseMatrix[Double], kNM: DenseMatrix[Double], likNoiseVar: Double, a:DenseMatrix[Double]): Double = {

    val aInv = inv(a)
    -0.5 * trace(aInv * (2d * likNoiseVar * kMM))
  }

  private def f3D(kMM: DenseMatrix[Double], kMN: DenseMatrix[Double], kNM: DenseMatrix[Double], y: DenseVector[Double], likNoiseVar: Double, a:DenseMatrix[Double]): Double = {
    val aInv = inv(a)

    val t1 = 1d / (2 * likNoiseVar)
    val t1D = -1d / likNoiseVar
    val t2 = y.t * kNM * aInv * kMN * y
    val t2D = -y.t * kNM * aInv * (2d * likNoiseVar * kMM) * aInv * kMN * y

    t1D*t2 + t1*t2D
  }

  private def f4D(lowerBoundTerm:Double): Double = -2*lowerBoundTerm

  private def f5D(lowerBoundTerm:Double): Double = -2*lowerBoundTerm

  

}