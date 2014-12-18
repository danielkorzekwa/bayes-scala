package dk.bayes.model.factorgraph

import dk.bayes.model.factor.api.Factor
import scala.collection.mutable.ArrayBuffer
import dk.bayes.model.factor.api.TripleFactor
import dk.bayes.model.factor.api.SingleFactor
import dk.bayes.model.factor.api.DoubleFactor

/**
 * This class represents a node in a factor graph, either Factor or Variable.
 *
 * @author Daniel Korzekwa
 *
 */
sealed abstract class Node {

  /**
   * Returns true if the node is calibrated
   *
   * @param threshold The maximum absolute difference between the old and the new corresponding outgoing messages for the node's gates
   */
  def isCalibrated(threshold: Double): Boolean
}

/**
 * This class represents a factor node in a factor graph.
 *
 * @param factor Factor associated with a factor node
 */
sealed abstract class FactorNode(factor: Factor) extends Node {

  private var _factor: Factor = factor

  def getFactor(): Factor = _factor
  def setFactor(factor: Factor) { _factor = factor }

  /**
   * Returns the product of the factor and all incoming messages.
   */
  def factorMarginal(): Factor

}

case class SingleFactorNode(factor: SingleFactor, gate: FactorGate) extends FactorNode(factor) {

  def isCalibrated(threshold: Double): Boolean = {
    val notCalibratedGate = Vector(gate).find(g => !g.getMessage().equals(g.getOldMessage(), threshold))
    notCalibratedGate.isEmpty
  }

  def factorMarginal(): Factor = {
    val inMsg = gate.getEndGate().getMessage()
    val factorMarginal = factor * inMsg
    factorMarginal
  }
}

case class DoubleFactorNode(factor: DoubleFactor, gate1: FactorGate, gate2: FactorGate) extends FactorNode(factor) {

  def isCalibrated(threshold: Double): Boolean = {
    val notCalibratedGate = Vector(gate1, gate2).find(g => !g.getMessage().equals(g.getOldMessage(), threshold))
   
    notCalibratedGate.isEmpty
  }

  def factorMarginal(): Factor = {
    val inMsg1 = gate1.getEndGate().getMessage()
    val inMsg2 = gate2.getEndGate().getMessage()

    val factorMarginal = factor * inMsg1 * inMsg2
    factorMarginal
  }
}

case class TripleFactorNode(factor: TripleFactor, gate1: FactorGate, gate2: FactorGate, gate3: FactorGate) extends FactorNode(factor) {

  def isCalibrated(threshold: Double): Boolean = {
    val notCalibratedGate = Vector(gate1, gate2, gate3).find(g => !g.getMessage().equals(g.getOldMessage(), threshold))
    notCalibratedGate.isEmpty
  }

  def factorMarginal(): Factor = {
    val inMsg1 = gate1.getEndGate().getMessage()
    val inMsg2 = gate2.getEndGate().getMessage()
    val inMsg3 = gate3.getEndGate().getMessage()
    val factorMarginal = factor * inMsg1 * inMsg2 * inMsg3
    factorMarginal
  }

}

case class GenericFactorNode(factor: Factor, gates: Seq[FactorGate]) extends FactorNode(factor) {
  def isCalibrated(threshold: Double): Boolean = {
    val notCalibratedGate = gates.find(g => !g.getMessage().equals(g.getOldMessage(), threshold))
    notCalibratedGate.isEmpty
  }
  def factorMarginal() = throw new UnsupportedOperationException("Not implemented")
}

/**
 * This class represents a variable node in a factor graph.
 *
 * @param varId Unique variable id
 */
case class VarNode(varId: Int) extends Node {

  private var gates = ArrayBuffer[VarGate]()

  /**
   * Adds an outgoing gate to a node.
   */
  def addGate(gate: VarGate) = gates += gate

  /**
   * Returns outgoing gates for a node.
   */
  def getGates(): IndexedSeq[VarGate] = gates

  def isCalibrated(threshold: Double): Boolean = {
    val notCalibratedGate = getGates.find(g => !g.getMessage().equals(g.getOldMessage(), threshold))
   
    notCalibratedGate.isEmpty
  }
}