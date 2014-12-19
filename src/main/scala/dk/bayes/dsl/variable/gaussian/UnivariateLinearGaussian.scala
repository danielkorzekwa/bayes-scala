package dk.bayes.dsl.variable.gaussian

import dk.bayes.dsl.Variable
import dk.bayes.math.linear.Matrix
import dk.bayes.dsl.InferEngine
import dk.bayes.dsl.variable.Gaussian

/**
 * y = A*x + b + gaussian_noise
 *
 * @author Daniel Korzekwa
 */
class UnivariateLinearGaussian(val a: Matrix, val x: Seq[Gaussian], val b: Double, val v: Double, val yValue: Option[Double] = None) extends Gaussian {

  def getParents(): Seq[Variable] = x
}
