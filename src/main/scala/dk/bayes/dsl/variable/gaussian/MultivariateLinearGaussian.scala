package dk.bayes.dsl.variable.gaussian

import dk.bayes.math.linear.Matrix
import dk.bayes.dsl.Variable
import dk.bayes.dsl.InferEngine
import dk.bayes.dsl.variable.Gaussian
import dk.bayes.dsl.variable.gaussian.infer.inferMultivariateLinearGaussianSimplest

/**
 * y = A*x + b + gaussian_noise
 *
 * @author Daniel Korzekwa
 */
class MultivariateLinearGaussian(val a: Matrix, val x: MultivariateGaussian, val b: Matrix, val v: Matrix, val yValue: Option[Double]) extends Gaussian {

  def getParents(): Seq[Variable] = List(x)
}

object MultivariateLinearGaussian {

  implicit val inferEngine = Vector(inferMultivariateLinearGaussianSimplest)
}