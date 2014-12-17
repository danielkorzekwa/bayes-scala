package dk.bayes.dsl.variable.gaussian

import dk.bayes.math.linear.Matrix

/**
 * N(m,v)
 *
 * @author Daniel Korzekwa
 */
object Gaussian {

  /**
   * N(m,v)
   */
  def apply(m: Double, v: Double) = new UnivariateGaussian(m, v)

  /**
   * y = x + gaussian_noise
   */
  def apply(x: UnivariateGaussian, v: Double, value: Option[Double] = None) = new UnivariateLinearGaussian(a = 1, x, b = 0, v, value)

  /**
   * N(m,v)
   */
  def apply(m: Matrix, v: Matrix) = MultivariateGaussian(m, v)

  /**
   * y = A*x + b + gaussian_noise
   */
  def apply(A: Matrix, x: MultivariateGaussian, b: Matrix, v: Matrix) = new MultivariateLinearGaussian(A, x, b, v)
}