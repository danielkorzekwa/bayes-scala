package dk.bayes.dsl.variable.gaussian.multivariatelinear

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import dk.bayes.dsl.Variable
import dk.bayes.dsl.variable.Gaussian
import dk.bayes.dsl.variable.gaussian.multivariate.MultivariateGaussian

/**
 * y = A*x + b + gaussian_noise
 *
 * @author Daniel Korzekwa
 */
class MultivariateLinearGaussian(val a: DenseMatrix[Double], val x: MultivariateGaussian, val b: DenseVector[Double], val v: DenseMatrix[Double], val yValue: Option[DenseVector[Double]]) extends Gaussian {

  def getParents(): Seq[Variable] = List(x)
}

object MultivariateLinearGaussian {

  implicit val inferEngines = Vector(inferMultivariateLinearGaussianSimplest)
}