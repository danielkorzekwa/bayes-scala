package dk.bayes.infer.ep

import dk.bayes.model.factorgraph._
import dk.bayes.model.factor.Factor
import dk.bayes.model.factorgraph.FactorNode
import dk.bayes.model.factorgraph.VarNode
import com.typesafe.scalalogging.slf4j.Logger
import org.slf4j.LoggerFactory
import dk.bayes.model.factorgraph.VarGate
import dk.bayes.model.factorgraph.FactorNode
import dk.bayes.model.factor.TableFactor
import dk.bayes.model.factor.TableFactor
import dk.bayes.model.factor.GaussianFactor
import dk.bayes.model.factor.GaussianFactor
import dk.bayes.model.factor.TableFactor
import GenericEP._

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

  def calibrate(maxIter: Int, currIterProgress: (Int) => Unit, messageOrder: MessageOrder = ForwardBackwardMsgOrder()): Int = {

    var currIter = 0
    var calibrated = false
    while (!calibrated && currIter < maxIter) {
      currIterProgress(currIter)

      val nodes = factorGraph.getNodes()
      val orderedNodes = messageOrder.ordered(nodes)
      calibrateIteration(orderedNodes)

      calibrated = isCalibrated()
      currIter += 1
    }

    currIter
  }

  /**
   * Executes a single message passing routine on a factor graph.
   *
   */
  private def calibrateIteration(nodes: Seq[Node]) {

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
      val marginalNode = factorNode.getFactor().productMarginal(marginalVarId, inMsgs)
      val newMessage = marginalNode / gate.getEndGate.getMessage

      gate.setMessage(newMessage)
      logger.debug("from: %s\t to: %s\t\t msg: %s".format(factorNode.getFactor().getVariableIds.mkString("f(", ",", ")"), gate.getEndGate.varId, newMessage))
    }
  }

  private def sendVariableMessage(varNode: VarNode) {
    val inMsgs = varNode.getGates().map(g => g.getEndGate.getMessage())

    val marginalFactor = inMsgs.reduceLeft((msg1, msg2) => msg1 * msg2)
    for (gate <- varNode.getGates()) {

      val newMessage = marginalFactor / gate.getEndGate.getMessage
      gate.setMessage(newMessage)
      logger.debug("from: %s\t\t to: %s\t msg: %s".format(varNode.varId, gate.getEndGate.factorNode.getFactor().getVariableIds.mkString("f(", ",", ")"), newMessage))
    }
  }

  /**
   * Returns true if factor graph is calibrated, false otherwise.
   */
  private def isCalibrated(): Boolean = {

    val notCalibratedNode = factorGraph.getNodes().find { node =>
      val notCalibratedGate = node.getGates.find(g => !g.getMessage().equals(g.getOldMessage(), threshold))
      notCalibratedGate.isDefined
    }
    notCalibratedNode.isEmpty
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
        val inMsgs = factorNode.getGates().map(g => g.getEndGate().getMessage())
        val factorMarginal = inMsgs.foldLeft(factorNode.getFactor())((product, msg) => product * msg)
        factorMarginal
      }
    }

  }

}

object GenericEP {

  case class ForwardMsgOrder extends MessageOrder {
    def ordered(nodes: Seq[Node]): Seq[Node] = nodes
  }

  case class ForwardBackwardMsgOrder extends MessageOrder {
    def ordered(nodes: Seq[Node]): Seq[Node] = nodes ++ nodes.reverse
  }

}