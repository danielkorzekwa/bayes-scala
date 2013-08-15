package dk.bayes.math.gaussian

import dk.bayes.math.gaussian.Linear._

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
   * @param xId Unique id of variable x in a canonical form,  where y ~ Normal(ax+b,v)
   * @param yId Unique id of variable y in a canonical form,  where y ~ Normal(ax+b,v)
   */
  def toCanonical(xId: Int, yId: Int): CanonicalGaussian = CanonicalGaussian(Array(xId, yId), Matrix(a), b, v)
}