package dk.bayes.gaussian

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
  
}