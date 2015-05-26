package dk.bayes.infer.gp.sgpr

import breeze.linalg.DenseMatrix
import breeze.linalg.inv
import breeze.linalg.DenseVector
import scala.math._
import breeze.linalg.trace
import breeze.linalg.sum

/**
 * Calculates derivatives of variational lower bound with respect to covariance hyper parameters for Sparse GP regression model.
 *
 * Based on:
 * - Variational Learning of Inducing Variables in Sparse Gaussian Processes (http://jmlr.org/proceedings/papers/v5/titsias09a/titsias09a.pdf)
 * - Derivatives of lower bound, Michalis K. Titsias
 */
object calcLowerBoundDKernel {

   /**
   *
   * @param a likNoiseVar*kMM + kMN*kNM //[m x m]
   */
  def apply(kNNDiagDArray: Array[DenseVector[Double]], kMM: DenseMatrix[Double], kMMinv: DenseMatrix[Double], kMMdArray: Array[DenseMatrix[Double]], kMN: DenseMatrix[Double], kNM: DenseMatrix[Double], kNMdArray: Array[DenseMatrix[Double]], 
      y: DenseVector[Double], likNoiseVar: Double,a:DenseMatrix[Double]): Array[Double] = {

    val expensiveTerm = calcExpensiveTerm(kMMinv, kMN, kNM, y, likNoiseVar, a)

    val dArray = (0 until kMMdArray.size).map { i =>
      val kMMd = kMMdArray(i)
      val kNMd = kNMdArray(i)
      val kNNDiagD = kNNDiagDArray(i)

      val t1247 = calcTerms_1_2_4_7(kMM, kMMinv, kMMd, kMN, kNM, y, likNoiseVar, expensiveTerm)
      val t3568 = calcTerms_3_5_6_8(kMM, kMMinv, kMN, kNM, kNMd, y, likNoiseVar, a, expensiveTerm)

      val f4 = -0.5 / likNoiseVar * sum(kNNDiagD)
      t1247 + t3568 + f4
    }.toArray

    dArray
  }

  private def calcTerms_1_2_4_7(kMM: DenseMatrix[Double], kMMinv: DenseMatrix[Double], kMMd: DenseMatrix[Double], kMN: DenseMatrix[Double], kNM: DenseMatrix[Double],
                                y: DenseVector[Double], likNoiseVar: Double, expensiveTerm: DenseMatrix[Double]): Double = {
    val t1 = expensiveTerm
    val t2 = (kMMinv / likNoiseVar) * kMN * kNM * (kMMinv / likNoiseVar)
    0.5 * likNoiseVar * trace(kMMd * (t1 - t2))
  }
  private def calcTerms_3_5_6_8(kMM: DenseMatrix[Double], kMMinv: DenseMatrix[Double], kMN: DenseMatrix[Double], kNM: DenseMatrix[Double], kNMd: DenseMatrix[Double],
                                y: DenseVector[Double], likNoiseVar: Double, a: DenseMatrix[Double], expensiveTerm: DenseMatrix[Double]): Double = {
  
    val t1 = expensiveTerm // [m x m]
    val t2 = ((inv(a) * kMN * y) / likNoiseVar) * y.t // [m x n]
    val t3568 = trace((t1 * kMN + t2) * kNMd) //tr[A*B]=tr[B*A]
    t3568
  }
  
  private def calcExpensiveTerm(kMMinv: DenseMatrix[Double], kMN: DenseMatrix[Double], kNM: DenseMatrix[Double], y: DenseVector[Double], likNoiseVar: Double, a: DenseMatrix[Double]): DenseMatrix[Double] = {
    val aInv = inv(a)
    val t1 = (aInv * kMN * y) / sqrt(likNoiseVar)
    val t2 = (y.t * kNM * aInv) / sqrt(likNoiseVar)
    kMMinv / likNoiseVar - aInv - t1 * t2
  }
}