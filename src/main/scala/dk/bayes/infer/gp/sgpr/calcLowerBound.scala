package dk.bayes.infer.gp.sgpr

import scala.math._
import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import breeze.linalg.logdet
import breeze.linalg.inv
import breeze.linalg.trace
import breeze.linalg.sum

/**
 * Calculates the value of variational lower bound for Sparse GP regression model.
 *
 * Based on:
 * - Variational Learning of Inducing Variables in Sparse Gaussian Processes (http://jmlr.org/proceedings/papers/v5/titsias09a/titsias09a.pdf)
 * - Derivatives of lower bound, Michalis K. Titsias
 */
object calcLowerBound {

  /**
   * Returns terms 0,1,2,3,4,5 of the lower bound. Just sum them up.
   *
   * @param a likNoiseVar*kMM + kMN*kNM
   */
  def apply(kMM: DenseMatrix[Double], kMMinv: DenseMatrix[Double], kMN: DenseMatrix[Double], kNM: DenseMatrix[Double], kNNdiag: DenseVector[Double],
            y: DenseVector[Double], likNoiseVar: Double, n: Double, m: Double,
            a: DenseMatrix[Double]): Array[Double] = {

    val f0Term = f0(y, likNoiseVar, n, m)
    val f1Term = f1(kMM)
    val f2Term = f2(kMM, kMN, kNM, likNoiseVar, a)
    val f3Term = f3(kMM, kMN, kNM, y, likNoiseVar, a)
    val f4Term = f4(kNNdiag, likNoiseVar)
    val f5Term = f5(kMMinv, kMN, kNM, likNoiseVar)

    Array(f0Term, f1Term, f2Term, f3Term, f4Term, f5Term)
  }
  private def f0(y: DenseVector[Double], likNoiseVar: Double, n: Double, m: Double): Double =
    (-n / 2) * log(2 * Pi) - ((n - m).toDouble / 2) * log(likNoiseVar) - (1d / (2 * likNoiseVar)) * (y.t * y)

  private def f1(kMM: DenseMatrix[Double]): Double = 0.5 * logdet(kMM)._2

  private def f2(kMM: DenseMatrix[Double], kMN: DenseMatrix[Double], kNM: DenseMatrix[Double], likNoiseVar: Double, a: DenseMatrix[Double]): Double =
    -0.5 * logdet(a)._2

  private def f3(kMM: DenseMatrix[Double], kMN: DenseMatrix[Double], kNM: DenseMatrix[Double], y: DenseVector[Double], likNoiseVar: Double, a: DenseMatrix[Double]): Double =
    (1d / (2 * likNoiseVar)) * (y.t * kNM * inv(a) * kMN * y)

  private def f4(kNNdiag: DenseVector[Double], likNoiseVar: Double) = -(1d / (2 * likNoiseVar)) * sum(kNNdiag)

  private def f5(kMMinv: DenseMatrix[Double], kMN: DenseMatrix[Double], kNM: DenseMatrix[Double], likNoiseVar: Double) = {
    (1d / (2 * likNoiseVar)) * trace(kMMinv * (kMN * kNM))
  }

}