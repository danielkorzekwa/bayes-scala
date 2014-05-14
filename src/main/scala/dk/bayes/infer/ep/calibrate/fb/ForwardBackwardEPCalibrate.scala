package dk.bayes.infer.ep.calibrate.fb

import dk.bayes.model.factorgraph.FactorGraph
import java.util.concurrent.atomic.AtomicLong
import dk.bayes.model.factorgraph.FactorNode
import dk.bayes.model.factorgraph.VarNode
import com.typesafe.scalalogging.slf4j.Logger
import org.slf4j.LoggerFactory
import dk.bayes.model.factorgraph.Node
import dk.bayes.model.factor.api.Factor
import dk.bayes.model.factor.api.SingleFactor
import dk.bayes.model.factorgraph.GenericFactorGraph
import dk.bayes.model.factorgraph.TripleFactorNode
import dk.bayes.model.factor.api.TripleFactor
import dk.bayes.model.factorgraph.DoubleFactorNode
import dk.bayes.model.factorgraph.SingleFactorNode
import dk.bayes.model.factor.api.DoubleFactor
import dk.bayes.model.factorgraph.GenericFactorNode
import dk.bayes.model.factor.api.GenericFactor

/**
 * Calibrates factor graph with forward-backward passes over all factor and variable nodes.
 *
 * @author Daniel Korzekwa
 *
 * @param factorGraph Factor graph to be calibrated
 * @param threshold Calibration criteria: the maximum absolute difference between old and new corresponding messages on a factor graph
 */
case class ForwardBackwardEPCalibrate(factorGraph: FactorGraph, threshold: Double = 0.00001) {

  private val logger = Logger(LoggerFactory.getLogger(getClass()))

  /**
   * Calibrates factor graph.
   *
   * @param maxIter The maximum number of iterations that EP is executed for
   * @param currIterProgress Current iteration number. It is called by the calibrate method at the beginning of every iteration
   *
   * @return EP execution summary
   */
  def calibrate(maxIter: Int, currIterProgress: (Int) => Unit): EPSummary = {

    var newMsgIndex = new AtomicLong(0)
    def nextMsgIndex() = newMsgIndex.getAndIncrement()

    var currIter = 0
    var calibrated = false

    val nodes = factorGraph.getNodes()
    val orderedNodes = nodes ++ nodes.reverse

    while (!calibrated && currIter < maxIter) {
      currIterProgress(currIter)

      calibrateIteration(orderedNodes, nextMsgIndex)

      calibrated = isCalibrated()

      currIter += 1
    }

    EPSummary(currIter, newMsgIndex.get())
  }

  /**
   * Executes a single message passing routine on a factor graph.
   *
   */
  private def calibrateIteration(nodes: Seq[Node], newMsgIndex: () => Long) {

    val nodesNum = nodes.size

    var i = 0
    while (i < nodesNum) {
      nodes(i) match {
        case node: FactorNode => sendFactorMessage(node, newMsgIndex)
        case node: VarNode => sendVariableMessage(node, newMsgIndex)
      }
      i += 1
    }
  }

  private def sendFactorMessage(factorNode: FactorNode, newMsgIndex: () => Long) {

    factorNode match {
      case factorNode: SingleFactorNode => sendFactorMessage(factorNode, newMsgIndex)
      case factorNode: DoubleFactorNode => sendFactorMessage(factorNode, newMsgIndex)
      case factorNode: TripleFactorNode => sendFactorMessage(factorNode, newMsgIndex)
      case factorNode: GenericFactorNode => sendFactorMessage(factorNode, newMsgIndex)
    }

  }

  private def sendFactorMessage(factorNode: SingleFactorNode, newMsgIndex: () => Long) {
    val newMessage = factorNode.getFactor().asInstanceOf[SingleFactor]
   
    factorNode.gate.setMessage(newMessage, newMsgIndex())

    logger.debug("from: %s\t to: %s\t\t msg: %s".format(factorNode.getFactor().getVariableIds.mkString("f(", ",", ")"), factorNode.gate.getEndGate.varNode.varId, newMessage))

  }

  private def sendFactorMessage(factorNode: DoubleFactorNode, newMsgIndex: () => Long) {

    val gate1MsgIn = factorNode.gate1.getEndGate.getMessage
    val gate2MsgIn = factorNode.gate2.getEndGate.getMessage

    val factor = factorNode.getFactor().asInstanceOf[DoubleFactor]
    val (newMessageGate1, newMessageGate2) = factor.outgoingMessages(gate1MsgIn, gate2MsgIn)

    factorNode.gate1.setMessage(newMessageGate1, newMsgIndex())
    logger.debug("from: %s\t to: %s\t\t msg: %s".format(factorNode.getFactor().getVariableIds.mkString("f(", ",", ")"), factorNode.gate1.getEndGate.varNode.varId, newMessageGate1))

    factorNode.gate2.setMessage(newMessageGate2, newMsgIndex())
    logger.debug("from: %s\t to: %s\t\t msg: %s".format(factorNode.getFactor().getVariableIds.mkString("f(", ",", ")"), factorNode.gate2.getEndGate.varNode.varId, newMessageGate2))

  }

  private def sendFactorMessage(factorNode: TripleFactorNode, newMsgIndex: () => Long) {

    val gate1MsgIn = factorNode.gate1.getEndGate.getMessage
    val gate2MsgIn = factorNode.gate2.getEndGate.getMessage
    val gate3MsgIn = factorNode.gate3.getEndGate.getMessage

    val factor = factorNode.getFactor().asInstanceOf[TripleFactor]
    val (newMessageGate1, newMessageGate2, newMessageGate3) = factor.outgoingMessages(gate1MsgIn, gate2MsgIn, gate3MsgIn)

    factorNode.gate1.setMessage(newMessageGate1, newMsgIndex())
    logger.debug("from: %s\t to: %s\t\t msg: %s".format(factorNode.getFactor().getVariableIds.mkString("f(", ",", ")"), factorNode.gate1.getEndGate.varNode.varId, newMessageGate1))

    factorNode.gate2.setMessage(newMessageGate2, newMsgIndex())
    logger.debug("from: %s\t to: %s\t\t msg: %s".format(factorNode.getFactor().getVariableIds.mkString("f(", ",", ")"), factorNode.gate2.getEndGate.varNode.varId, newMessageGate2))

    factorNode.gate3.setMessage(newMessageGate3, newMsgIndex())
    logger.debug("from: %s\t to: %s\t\t msg: %s".format(factorNode.getFactor().getVariableIds.mkString("f(", ",", ")"), factorNode.gate3.getEndGate.varNode.varId, newMessageGate3))

  }

  private def sendFactorMessage(factorNode: GenericFactorNode, newMsgIndex: () => Long) {
    val msgsIn = factorNode.gates.map(g => g.getEndGate.getMessage())
    val factor = factorNode.getFactor().asInstanceOf[GenericFactor]

    val newMsgsOut = factor.outgoingMessages(msgsIn)

    newMsgsOut.zip(factorNode.gates).foreach {
      case (msg, gate) =>
        gate.setMessage(msg, newMsgIndex())

        logger.debug("from: %s\t to: %s\t\t msg: %s".format(factorNode.getFactor().getVariableIds.mkString("f(", ",", ")"), gate.getEndGate.varNode.varId, msg))
    }

  }

  /**Returns the number of messages sent.*/
  private def sendVariableMessage(varNode: VarNode, newMsgIndex: () => Long) {

    var marginalFactor = varNode.getGates()(0).getEndGate.getMessage()
    var i = 1
    val gatesNum = varNode.getGates().size
    while (i < gatesNum) {
      marginalFactor = marginalFactor * varNode.getGates()(i).getEndGate.getMessage()
      i += 1
    }

    i = 0
    while (i < gatesNum) {
      val gate = varNode.getGates()(i)
      val newMessage = marginalFactor / gate.getEndGate.getMessage
      gate.setMessage(newMessage, newMsgIndex())

      logger.debug("from: %s\t\t to: %s\t msg: %s".format(varNode.varId, gate.getEndGate.getFactorNode().getFactor().getVariableIds.mkString("f(", ",", ")"), newMessage))
      i += 1
    }
  }

  /**
   * Returns true if factor graph is calibrated, false otherwise.
   */
  private def isCalibrated(): Boolean = {

    val notCalibratedNode = factorGraph.getNodes().find { node => !node.isCalibrated(threshold) }
    notCalibratedNode.isEmpty
  }

}