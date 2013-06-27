package dk.bayes.infer.ep.calibrate.lrug

import dk.bayes.model.factorgraph.FactorNode
import dk.bayes.model.factorgraph.VarNode
import com.typesafe.scalalogging.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicLong
import dk.bayes.model.factorgraph.FactorGraph
import scala.util.Random
import dk.bayes.model.factorgraph.Gate
import dk.bayes.model.factorgraph.FactorGate
import dk.bayes.model.factorgraph.VarGate
import dk.bayes.model.factorgraph.Node

/**
 * Calibrates factor graph by passing messages one-by-one between factor and variable nodes.
 * The next message is passed to the least recently visited gate.
 *
 * @author Daniel Korzekwa
 *
 *  @param factorGraph Factor graph to be calibrated
 * @param threshold Calibration criteria: the maximum absolute difference between old and new corresponding messages on a factor graph
 */
case class LRUGateEPCalibrate(factorGraph: FactorGraph, threshold: Double = 0.00001) {

  private val logger = Logger(LoggerFactory.getLogger(getClass()))

  def calibrate(): Long = {

    var newMsgIndex = new AtomicLong(0)
    def nextMsgIndex() = newMsgIndex.getAndIncrement()

    calibrateIteration(factorGraph.getNodes(), nextMsgIndex)

    newMsgIndex.get()
  }

  /**
   * Executes a single message passing routine on a factor graph.
   *
   */
  private def calibrateIteration(nodes: Seq[Node], newMsgIndex: () => Long) {

    var stop = false
    var nextGate = getNextGate(nodes.head.getGates().head)
    while (!stop) {

      nextGate.get match {
        case g: VarGate => {

          val inMsgs = g.varNode.getGates().map(g => g.getEndGate.getMessage())
          val marginalFactor = inMsgs.reduceLeft((msg1, msg2) => msg1 * msg2)
          val newMessage = marginalFactor / g.getEndGate.getMessage
          g.setMessage(newMessage, newMsgIndex())
          logger.debug("from: %s\t\t to: %s\t msg: %s".format(g.varNode.varId, g.getEndGate.factorNode.getFactor().getVariableIds.mkString("f(", ",", ")"), newMessage))
        }
        case g: FactorGate =>
          {
            val inMsgs = g.factorNode.getGates().map(g => g.getEndGate.getMessage())

            val marginalVarId = g.getMessage.getVariableIds.head
            val marginalNode = g.factorNode.getFactor().productMarginal(marginalVarId, inMsgs)
            val newMessage = marginalNode / g.getEndGate.getMessage
            g.setMessage(newMessage, newMsgIndex())
            logger.debug("from: %s\t to: %s\t\t msg: %s".format(g.factorNode.getFactor().getVariableIds.mkString("f(", ",", ")"), g.getEndGate.varNode.varId, newMessage))
          }

          stop = isCalibrated()
      }

      nextGate = getNextGate(nextGate.get)
    }

  }

  private def getNextGate(prevGate: Gate): Option[Gate] = {
    val nextGate = prevGate.getEndGate() match {
      case varGate: VarGate => {
        val sortedGates = if (varGate.varNode.getGates.size > 1) varGate.varNode.getGates.sortWith((g1, g2) => g1.getMsgIndex() < g2.getMsgIndex()).filterNot(g => g.getEndGate.getMsgIndex == prevGate.getMsgIndex && prevGate.getMsgIndex > -1)
        else varGate.varNode.getGates
        Some(sortedGates.head)
      }
      case factorGate: FactorGate => {
        val sortedGates = if (factorGate.factorNode.getGates.size > 1) factorGate.factorNode.getGates.sortWith((g1, g2) => g1.getMsgIndex() < g2.getMsgIndex()).filterNot(g => g.getEndGate.getMsgIndex == prevGate.getMsgIndex && prevGate.getMsgIndex > -1)
        else factorGate.factorNode.getGates
        Some(sortedGates.head)
      }
    }
    nextGate
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

}
