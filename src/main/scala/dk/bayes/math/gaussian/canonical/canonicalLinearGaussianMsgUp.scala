package dk.bayes.math.gaussian.canonical

import breeze.linalg.DenseMatrix
import breeze.linalg.cholesky
import breeze.linalg.inv
import breeze.linalg.DenseVector

/**
 * Computes integral of p(y|x)*p(y)dy,
 *
 * Following up:
 * - Bishop's book chapter 2.3 The Gaussian Distribution
 * - Daphne Koller's book on Probabilistic Graphical Models, chapter on Canonical Gaussian
 */
object canonicalLinearGaussianMsgUp {

  /**
   * @param a,b,v p(y|x) = N(ax+b,v)
   * @param y p(y)
   */
  def apply(a: DenseMatrix[Double], b: DenseVector[Double], v: DenseMatrix[Double], y: DenseCanonicalGaussian): DenseCanonicalGaussian = {

    val linv = inv(cholesky(v).t)
    val vInv = linv * linv.t

    val k00linv = a.t * linv
    val k00 = k00linv * k00linv.t // a.t * vInv * a
    val k01 = (a.t * (-1d)) * vInv
    val k10 = (vInv * (-1d)) * a
    val k11 = vInv

    val h00 = (-1d * a.t) * vInv * b
    val h01 = vInv * b

    val k11sum = k11 + y.k
    val k11sumLInv = inv(cholesky(k11sum).t)
    val k11sumInv = k11sumLInv * k11sumLInv.t
    val h01sum = h01 + y.h

    val k01k11sumLInv = k01 * k11sumLInv
    val k0 = k00 - k01k11sumLInv * k01k11sumLInv.t

    val h0 = h00 - k01 * k11sumInv * h01sum

    new DenseCanonicalGaussian(k0, h0, Double.NaN)
  }
}