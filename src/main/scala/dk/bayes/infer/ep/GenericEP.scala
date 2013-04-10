package dk.bayes.infer.ep

import dk.bayes.model.factorgraph.FactorGraph
import dk.bayes.model.factor.Factor
import dk.bayes.model.factorgraph.FactorNode
import dk.bayes.model.factorgraph.VarNode

/**
 * Default implementation of the Expectation Propagation Bayesian Inference algorithm.
 *
 * @author Daniel Korzekwa
 */
case class GenericEP(factorGraph: FactorGraph) extends EP {

  def setEvidence(varId: Int, varValue: AnyVal) = throw new UnsupportedOperationException("Not implemented yet")

  def calibrate() = {

    val nodes = factorGraph.getNodes()

    for (node <- nodes) {
      node match {
        case FactorNode(factor) =>
        case VarNode(varId) =>
      }
    }
  }

  def marginal(variableId: Int): Factor = {

    val varNode = factorGraph.getVariableNode(variableId)

    val gates = varNode.getGates()

    val inMsgs = gates.map(g => g.getEndGate.message)

    val marginalFactor = inMsgs.reduceLeft((msg1, msg2) => msg1 * msg2)

    marginalFactor
  }

}