package dk.bayes.infer.ep

import dk.bayes.model.factorgraph.FactorGraph
import dk.bayes.model.factor.Factor

/**
 * Default implementation of the Expectation Propagation Bayesian Inference algorithm.
 *
 * @author Daniel Korzekwa
 */
case class GenericEP(factorGraph: FactorGraph) extends EP {

  def setEvidence(varId: Int, varValue: AnyVal) = throw new UnsupportedOperationException("Not implemented yet")

  def calibrate() = throw new UnsupportedOperationException("Not implemented yet")

  def marginal(variableId: Int): Factor = throw new UnsupportedOperationException("Not implemented yet")
}