package dk.bayes.dsl.variable

import dk.bayes.math.linear.Matrix
import dk.bayes.dsl.Variable
import dk.bayes.dsl.variable.gaussian.univariate.UnivariateGaussian
import dk.bayes.dsl.variable.gaussian.multivariate.MultivariateGaussian
import dk.bayes.dsl.variable.gaussian.univariatelinear.UnivariateLinearGaussian
import dk.bayes.dsl.variable.gaussian.univariatelinear.UnivariateLinearGaussian
import dk.bayes.dsl.variable.gaussian.multivariatelinear.MultivariateLinearGaussian

/**
 * N(m,v)
 *
 * @author Daniel Korzekwa
 */
trait Gaussian extends Variable

object Gaussian {

  /**
   * N(m,v)
   */
  def apply(m: Double, v: Double) = new UnivariateGaussian(m, v)

  /**
   * N(m,v)
   */
  def apply(m: Matrix, v: Matrix) = new MultivariateGaussian(m, v)

  /**
   * Constructors for UnivariateLinearGaussian
   */

  /**
   * y = x + gaussian_noise
   */
  def apply(x: UnivariateGaussian, v: Double, yValue: Double) = new UnivariateLinearGaussian(a = Matrix(1), Vector(x), b = 0, v, Some(yValue))
  def apply(x: UnivariateGaussian, v: Double) = new UnivariateLinearGaussian(a = Matrix(1), Vector(x), b = 0, v, None)

  /**
   * y = A*x + b + gaussian_noise
   */
  def apply(A: Matrix, x: Seq[Gaussian], v: Double) = new UnivariateLinearGaussian(A, x, b = 0, v, None)
  def apply(A: Matrix, x: Seq[Gaussian], b: Double, v: Double, yValue: Double) = new UnivariateLinearGaussian(A, x, b, v, Some(yValue))
  def apply(A: Matrix, x: Seq[Gaussian], b: Double, v: Double) = new UnivariateLinearGaussian(A, x, b, v, None)

  /**
   * Constructors for MultivariateLinearGaussian
   */

  /**
   * y = x + gaussian_noise
   */
  def apply(x: MultivariateGaussian, v: Matrix, yValue: Matrix) = new MultivariateLinearGaussian(Matrix.identity(x.m.size), x, Matrix.zeros(x.m.size, 1), v, Some(yValue))
  def apply(x: MultivariateGaussian, v: Matrix) = new MultivariateLinearGaussian(Matrix.identity(x.m.size), x, Matrix.zeros(x.m.size, 1), v, None)
  /**
   * y = A*x + b + gaussian_noise
   */
  def apply(A: Matrix, x: MultivariateGaussian, b: Matrix, v: Matrix, yValue: Matrix) = new MultivariateLinearGaussian(A, x, b, v, Some(yValue))
  def apply(A: Matrix, x: MultivariateGaussian, b: Matrix, v: Matrix) = new MultivariateLinearGaussian(A, x, b, v, None)
}