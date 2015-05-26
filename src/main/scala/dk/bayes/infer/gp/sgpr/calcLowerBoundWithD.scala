package dk.bayes.infer.gp.sgpr

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector

/**
 * Calculates the value of variational lower bound + derivatives for Sparse GP regression model.
 *
 * Based on:
 * - Variational Learning of Inducing Variables in Sparse Gaussian Processes (http://jmlr.org/proceedings/papers/v5/titsias09a/titsias09a.pdf)
 * - Derivatives of lower bound, Michalis K. Titsias
 */
object calcLowerBoundWithD {

  /**
   * Returns Tuple3(
   * the value of lower bound,
   * derivatives of variational lower bound with respect to covariance hyper parameters,
   * derivatives of variational lower bound with respect to likelihood log noise std dev
   * )
   */
  def apply(kMM: DenseMatrix[Double], kMMinv: DenseMatrix[Double], kMMdArray: Array[DenseMatrix[Double]],
            kMN: DenseMatrix[Double], kNM: DenseMatrix[Double], kNMdArray: Array[DenseMatrix[Double]],
            kNNdiag: DenseVector[Double], kNNDiagDArray: Array[DenseVector[Double]],
            y: DenseVector[Double], likNoiseVar: Double, n: Double, m: Double): Tuple3[Double, Array[Double], Double] = {

    val a = calcA(kMM, kMN, kNM, likNoiseVar)
    val lBTerms = calcLowerBound(kMM, kMMinv, kMN, kNM, kNNdiag, y, likNoiseVar, n, m, a)
   
    val lowerBound = lBTerms.sum
    val lowerBoundDKernel = calcLowerBoundDKernel(kNNDiagDArray, kMM, kMMinv, kMMdArray, kMN, kNM, kNMdArray, y, likNoiseVar, a)
    val lowerBoundDLikNoise = calcLowerBoundKLikNoise(kMM, kMMinv, kMN, kNM, kNNdiag, y, likNoiseVar, n, m, lBTerms, a)

    (lowerBound, lowerBoundDKernel, lowerBoundDLikNoise)
  }

  /**
   * Returns likNoiseVar*kMM + kMN*kNM
   */
  private def calcA(kMM: DenseMatrix[Double], kMN: DenseMatrix[Double], kNM: DenseMatrix[Double], likNoiseVar: Double): DenseMatrix[Double] =
    likNoiseVar * kMM + kMN * kNM

}