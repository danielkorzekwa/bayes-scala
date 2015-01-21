package dk.bayes.dsl.variable.gaussian.univariate

import dk.bayes.dsl.Variable
import dk.bayes.dsl.InferEngine
import dk.bayes.dsl.variable.Gaussian

/**
 * N(m,v)
 *
 * @author Daniel Korzekwa
 */
case class UnivariateGaussian(val m: Double, val v: Double) extends Gaussian with UnivariateGaussianFactor {
  
  def getParents(): Seq[Variable] = Nil
}

object UnivariateGaussian {

  implicit val inferEngines = Vector(
    inferUnivariateGaussianSimplest,
    inferUnivariateGaussianEPNaiveBayes,
    inferUnivariateGaussianFactorGraph)

}