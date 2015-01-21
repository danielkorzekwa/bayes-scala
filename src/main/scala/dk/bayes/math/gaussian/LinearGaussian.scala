package dk.bayes.math.gaussian

import dk.bayes.math.linear._
import dk.bayes.math.gaussian.canonical.CanonicalGaussian
import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian

/**
 * Linear Gaussian Model: N(m,v), where,
 * m = ax + b
 *
 * @author Daniel Korzekwa
 *
 * @param a Mean component: m = ax + b
 * @param b Mean component: m = ax + b
 * @param v Variance
 */
case class LinearGaussian(a: Double, b: Double, v: Double) {

  def *(gaussian: Gaussian): MultivariateGaussian = gaussian * this

  /**
   * Converts Linear Gaussian to a canonical form.
   *
   */
  def toCanonical(): DenseCanonicalGaussian = DenseCanonicalGaussian(Matrix(a), b, v)
}