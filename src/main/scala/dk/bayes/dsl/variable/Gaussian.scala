package dk.bayes.dsl.variable

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import dk.bayes.dsl.Variable
import dk.bayes.dsl.variable.gaussian.multivariate.MultivariateGaussian
import dk.bayes.dsl.variable.gaussian.multivariatelinear.MultivariateLinearGaussian
import dk.bayes.dsl.variable.gaussian.univariate.UnivariateGaussian
import dk.bayes.dsl.variable.gaussian.univariatelinear.UnivariateLinearGaussian
import dk.bayes.dsl.variable.gaussian.univariatelinear.UnivariateLinearGaussian

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
  def apply(m: DenseVector[Double], v: DenseMatrix[Double]) = new MultivariateGaussian(m, v)

  /**
   * Constructors for UnivariateLinearGaussian
   */

  /**
   * y = x + gaussian_noise
   */
  def apply(x: UnivariateGaussian, v: Double, yValue: Double) = new UnivariateLinearGaussian(a = DenseMatrix(1d), Vector(x), b = 0, v, Some(yValue))
  def apply(x: UnivariateGaussian, v: Double) = new UnivariateLinearGaussian(a = DenseMatrix(1d), Vector(x), b = 0, v, None)

  /**
   * y = A*x + b + gaussian_noise
   */
  def apply(A: DenseMatrix[Double], x: Seq[Gaussian], v: Double) = new UnivariateLinearGaussian(A, x, b = 0, v, None)
  def apply(A: DenseMatrix[Double], x: Seq[Gaussian], b: Double, v: Double, yValue: Double) = new UnivariateLinearGaussian(A, x, b, v, Some(yValue))
  def apply(A: DenseMatrix[Double], x: Seq[Gaussian], b: Double, v: Double) = new UnivariateLinearGaussian(A, x, b, v, None)

  /**
   * Constructors for MultivariateLinearGaussian
   */

  /**
   * y = x + gaussian_noise
   */
  def apply(x: MultivariateGaussian, v: DenseMatrix[Double], yValue: DenseVector[Double]) = new MultivariateLinearGaussian(DenseMatrix.eye[Double](x.m.size), x, DenseVector.zeros[Double](x.m.size), v, Some(yValue))
  def apply(x: MultivariateGaussian, v: DenseMatrix[Double]) = new MultivariateLinearGaussian(DenseMatrix.eye[Double](x.m.size), x, DenseVector.zeros[Double](x.m.size), v, None)
  /**
   * y = A*x + b + gaussian_noise
   */
  def apply(A: DenseMatrix[Double], x: MultivariateGaussian, b: DenseVector[Double], v: DenseMatrix[Double], yValue: DenseVector[Double]) = new MultivariateLinearGaussian(A, x, b, v, Some(yValue))
  def apply(A: DenseMatrix[Double], x: MultivariateGaussian, b: DenseVector[Double], v: DenseMatrix[Double]) = new MultivariateLinearGaussian(A, x, b, v, None)
}