package dk.bayes.factorgraph

import dk.bayes.factorgraph.factor.api.SingleFactor

/**
 * This class represents an outgoing gate fimport dk.bayes.factorgraph.VarNode
import dk.bayes.factorgraph.FactorNode
rom a factor/variable node in a factor graph.
 *
 * @author Daniel Korzekwa
 *
 * @param message The initial outgoing message sent through the gate
 */
sealed abstract class Gate(initialMsg: SingleFactor) {

  type END_GATE <: Gate

  private var endGate: Option[END_GATE] = None

  private var message: SingleFactor = initialMsg
  private var oldMessage: SingleFactor = initialMsg

  /**Allows for comparing the age between different messages and finding the message that was updated least recently.*/
  private var msgIndex: Long = -1

  def setEndGate(gate: END_GATE):Unit = { endGate = Some(gate) }
  def getEndGate(): END_GATE = endGate.get

  def setMessage(newMessage: SingleFactor, msgIndex: Long):Unit = {
    oldMessage = message
    message = newMessage

    this.msgIndex = msgIndex
  }

  def getMsgIndex(): Long = msgIndex
  def getMessage(): SingleFactor = message
  def getOldMessage(): SingleFactor = oldMessage
}

case class FactorGate(initialMsg: SingleFactor) extends Gate(initialMsg) {

  type END_GATE = VarGate

  var _factorNode: Option[FactorNode] = None

  def setFactorNode(factorNode: FactorNode) = _factorNode = Some(factorNode)
  def getFactorNode() = _factorNode.get

}
case class VarGate(initialMsg: SingleFactor, varNode: VarNode) extends Gate(initialMsg) {
  type END_GATE = FactorGate
}