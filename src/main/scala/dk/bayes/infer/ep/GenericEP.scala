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
        case node: FactorNode => sendFactorMessage(node)
        case node: VarNode => sendVariableMessage(node)
      }
    }
  }

  private def sendFactorMessage(factorNode: FactorNode) {
    val inMsgs = factorNode.getGates().map(g => g.getEndGate.getMessage())
    for (gate <- factorNode.getGates()) {

      val marginalVarId = gate.getMessage.getVariableIds.head
      val marginalNode = factorNode.factor.productMarginal(marginalVarId, inMsgs)
      val newMessage = marginalNode / gate.getEndGate.getMessage

      gate.setMessage(newMessage)
    }
  }

  private def sendVariableMessage(varNode: VarNode) {
    val inMsgs = varNode.getGates().map(g => g.getEndGate.getMessage())
    val marginalFactor = inMsgs.reduceLeft((msg1, msg2) => msg1 * msg2)
    for (gate <- varNode.getGates()) {

      val newMessage = marginalFactor / gate.getEndGate.getMessage
      gate.setMessage(newMessage)
    }
  }

  def marginal(variableId: Int): Factor = {

    val varNode = factorGraph.getVariableNode(variableId)

    val gates = varNode.getGates()

    val inMsgs = gates.map(g => g.getEndGate.getMessage())

    val marginalFactor = inMsgs.reduceLeft((msg1, msg2) => msg1 * msg2)

    marginalFactor
  }

}