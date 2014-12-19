package dk.bayes.dsl.variable.gaussian

import dk.bayes.dsl.Variable
import dk.bayes.dsl.InferEngine
import dk.bayes.dsl.variable.Gaussian
import dk.bayes.dsl.variable.gaussian.infer.inferUnivariateGaussianFactorGraph
import dk.bayes.dsl.variable.gaussian.infer.inferUnivariateGaussianPosteriorSimplest

/**
 * N(m,v)
 *
 * @author Daniel Korzekwa
 */
class UnivariateGaussian(val m: Double, val v: Double) extends Gaussian {

  def getParents(): Seq[Variable] = Nil
}

object UnivariateGaussian {

  implicit val inferEngine = Vector(
    inferUnivariateGaussianPosteriorSimplest,
    inferUnivariateGaussianFactorGraph)

}