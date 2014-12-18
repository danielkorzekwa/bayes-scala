package dk.bayes.dsl.variable

import dk.bayes.math.linear.Matrix
import dk.bayes.dsl.variable.gaussian.UnivariateGaussian
import dk.bayes.dsl.variable.gaussian.UnivariateLinearGaussian
import dk.bayes.dsl.variable.gaussian.MultivariateLinearGaussian
import dk.bayes.dsl.variable.gaussian.MultivariateGaussian

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
  def apply(x: UnivariateGaussian, v: Double, value: Double) = new UnivariateLinearGaussian(a = 1, x, b = 0, v, Some(value))

  /**
   * N(m,v)
   */
  def apply(m: Matrix, v: Matrix) = MultivariateGaussian(m, v)

  /**
   * y = A*x + b + gaussian_noise
   */
  def apply(A: Matrix, x: MultivariateGaussian, b: Matrix, v: Matrix) = new MultivariateLinearGaussian(A, x, b, v)
}