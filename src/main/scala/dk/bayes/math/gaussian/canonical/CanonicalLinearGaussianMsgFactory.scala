package dk.bayes.math.gaussian.canonical

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import breeze.linalg.InjectNumericOps
import breeze.linalg.cholesky
import breeze.linalg.inv

/**
 * Computes integrals of:
 * -  p(y|x)*p(y)dy,
 * -  p(x)*p(y|x)dx
 * 
 * Following up:
 * - Bishop's book chapter 2.3 The Gaussian Distribution
 * - Daphne Koller's book on Probabilistic Graphical Models, chapter on Canonical Gaussian
 *
 *  @param a,b,v p(y|x) = N(ax+b,v)
 */
case class CanonicalLinearGaussianMsgFactory(a: DenseMatrix[Double], b: DenseVector[Double], v: DenseMatrix[Double]) {

  private val linv = inv(cholesky(v).t)
  private val vInv = linv * linv.t

  private val k00linv = a.t * linv
  private val k00 = k00linv * k00linv.t // a.t * vInv * a
  private val k01 = (a.t * (-1d)) * vInv
  private val k10 = (vInv * (-1d)) * a
  private val k11 = vInv

  private val h0 = (-1d * a.t) * vInv * b
  private val h1 = vInv * b

  /**
   * @param y p(y)
   */
  def msgUp(y: DenseCanonicalGaussian): DenseCanonicalGaussian = {

    val k11sum = k11 + y.k
    val k11sumLInv = inv(cholesky(k11sum).t)
    val k11sumInv = k11sumLInv * k11sumLInv.t
    val h1sum = h1 + y.h

    val k01k11sumLInv = k01 * k11sumLInv
    val k0 = k00 - k01k11sumLInv * k01k11sumLInv.t

    val newh0 = h0 - k01 * k11sumInv * h1sum

    new DenseCanonicalGaussian(k0, newh0, Double.NaN)
  }

  /**
   * @param x p(x)
   */
  def msgDown(x: DenseCanonicalGaussian): DenseCanonicalGaussian = {

    val k00sum = k00 + x.k
    val k00sumLInv = inv(cholesky(k00sum).t)
    val k00sumInv = k00sumLInv * k00sumLInv.t
    val h0sum = h0 + x.h

    val k10k00sumLInv = k10 * k00sumLInv
    val k1 = k11 - k10k00sumLInv * k10k00sumLInv.t

    val newh1 = h1 - k10 * k00sumInv * h0sum

    new DenseCanonicalGaussian(k1, newh1, Double.NaN)
  }
}