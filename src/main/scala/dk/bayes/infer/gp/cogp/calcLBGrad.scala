package dk.bayes.infer.gp.cogp

import breeze.linalg.DenseVector
import breeze.linalg.DenseMatrix
import scala.math._
import breeze.linalg.inv
import breeze.linalg.sum
import breeze.linalg.diag

/**
 * Returns gradient of a lower bound with respect to expectation parameters of q(u).
 * Expectation parameters are eta1 = mean, eta2 = mean^2 + sigma^2.
 *
 * Gradient derivation for eta1:
 * L(m) = L(f(eta1)) = F(x)
 * F'(x) = L'(f(eta1))*f'(eta1) = L'(f(eta1)) = L'(m)
 *
 * similarly for eta2
 * L(S^2) = L(f(eta2) = F(x)
 * F'(x) = L'(f(eta2))*f'(eta2) = L'(f(eta2)) = L'(S^2)
 *
 * Nguyen et al. Collaborative Multi-output Gaussian Processes, 2014, relevant equations 19, 20, 23, 24
 *
 * Useful reading
 * Hensman et al. Gaussian Processes for Big Data, 2013, relevant equations 4, 5, 6, eq below 6
 *
 */
object calcLBGrad {

  /**
   * @param Index of Q variable
   * @param beta [Px1]
   * @param w [i x j]
   */
  def apply(j: Int, beta: DenseVector[Double], w: DenseMatrix[Double], S: DenseMatrix[Double], kZZ: DenseMatrix[Double], kXZ: DenseMatrix[Double], y: DenseMatrix[Double],
            mP: Array[DenseVector[Double]], mQ: Array[DenseVector[Double]]): Tuple2[DenseVector[Double], DenseMatrix[Double]] = {

    val eta1Grad = calcGradEta1(j, beta, w, S, y, mP, mQ, kZZ, kXZ)
    val eta2Grad = calcGradEta2(j, beta, w, S, kZZ, kXZ)

    (eta1Grad, eta2Grad)
  }

  private def calcGradEta1(j: Int, beta: DenseVector[Double], w: DenseMatrix[Double], S: DenseMatrix[Double], y: DenseMatrix[Double], mP: Array[DenseVector[Double]], mQ: Array[DenseVector[Double]],
                           kZZ: DenseMatrix[Double], kXZ: DenseMatrix[Double]): DenseVector[Double] = {

    val A = kXZ * inv(kZZ)

    val tmp = (0 until beta.size).map { i =>
      val betaVal = beta(i)
      val wVal = w(i, j)

      val othersJIdx = (0 until w.cols).filter(jIndex => jIndex != j)
      val wAm = if (othersJIdx.size > 0) {
        othersJIdx.map { jIndex => w(i, jIndex) * A * mQ(jIndex) }.toArray.sum
      } else DenseVector.zeros[Double](y.rows)

      val yVal = y(::, i) - A * mP(i) - wAm

      betaVal * wVal * A.t * yVal
    }.reduceLeft((total, x) => total + x)

    val eta1Grad = tmp - inv(S) * mQ(j)
    eta1Grad
  }

  private def calcGradEta2(j: Int, beta: DenseVector[Double], w: DenseMatrix[Double], S: DenseMatrix[Double], kZZ: DenseMatrix[Double], kXZ: DenseMatrix[Double]): DenseMatrix[Double] = {

    val A = kXZ * inv(kZZ)
    val tmp = (0 until beta.size).map { i =>
      val betaVal = beta(i)
      val w2 = pow(w(i, j), 2)
      betaVal * w2 * A.t * A
    }.reduceLeft((total, x) => total + x)

    val lambda = inv(kZZ) + tmp
    val eta2Grad = 0.5 * inv(S) - 0.5 * lambda
    eta2Grad

  }

  implicit class DenseVectorOps(seq: Array[DenseVector[Double]]) {
    def sum(): DenseVector[Double] = seq match {
      case Array(x) => x
      case seq      => seq.reduceLeft((total, x) => total + x)
    }
  }
}