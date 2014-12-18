package dk.bayes.infer.ep

import org.slf4j.LoggerFactory

import com.typesafe.scalalogging.slf4j.Logger

import dk.bayes.model.factor.api.Factor
import dk.bayes.model.factorgraph.FactorGraph
import dk.bayes.model.factorgraph.FactorNode

/**
 * Default implementation of the Expectation Propagation Bayesian Inference algorithm.
 *
 * @author Daniel Korzekwa
 *
 * @param threshold Calibration criteria: the maximum absolute difference between old and new corresponding messages on a factor graph,
 */
case class GenericEP(factorGraph: FactorGraph, threshold: Double = 0.00001) extends EP {

  private val logger = Logger(LoggerFactory.getLogger(getClass()))

  def setEvidence(varId: Int, varValue: AnyVal) = {

    val nodes = factorGraph.getNodes()
    for (node <- nodes) {
      node match {
        case node: FactorNode if (node.getFactor.getVariableIds.contains(varId)) => {
          val newFactor = node.getFactor().withEvidence(varId, varValue)
          node.setFactor(newFactor)
        }
        case _ => //do nothing
      }
    }
  }

  def marginal(variableId: Int, variablesIds: Int*): Factor = {

    variablesIds match {
      case Nil => {
        val varNode = factorGraph.getVariableNode(variableId)

        val gates = varNode.getGates()

        val inMsgs = gates.map(g => g.getEndGate.getMessage())

        val variableMarginal = inMsgs.reduceLeft((msg1, msg2) => msg1 * msg2)

        variableMarginal
      }
      case _ => {
        val allVarIds = variableId :: variablesIds.toList
        val factorNode = factorGraph.getFactorNode(allVarIds)
        val factorMarginal = factorNode.factorMarginal()
        factorMarginal
      }
    }

  }

}
