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
 *
 *  @param a,b,v p(y|x) = N(ax+b,v)
 */
case class CanonicalLinearGaussianMsgUpFactory(a: DenseMatrix[Double], b: DenseVector[Double], v: DenseMatrix[Double]) {

  private val linv = inv(cholesky(v).t)
  private val vInv = linv * linv.t

  private val k00linv = a.t * linv
  private val k00 = k00linv * k00linv.t // a.t * vInv * a
  private val k01 = (a.t * (-1d)) * vInv
  private val k10 = (vInv * (-1d)) * a
  private val k11 = vInv

  private val h00 = (-1d * a.t) * vInv * b
  private val h01 = vInv * b

  /**
   * @param y p(y)
   */
  def msgUp(y: DenseCanonicalGaussian): DenseCanonicalGaussian = {

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