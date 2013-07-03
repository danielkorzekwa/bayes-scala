package dk.bayes.model.factorgraph

import dk.bayes.model.factor.Factor
import dk.bayes.model.factor.GaussianFactor
import dk.bayes.model.factor.SingleFactor

/**
 * This class represents an outgoing gate from a factor/variable node in a factor graph.
 *
 * @author Daniel Korzekwa
 *
 * @param message The initial outgoing message sent through the gate
 */
sealed abstract class Gate {

  type END_GATE <: Gate

  private var endGate: Option[END_GATE] = None

  private var message: Option[SingleFactor] = None
  private var oldMessage: Option[SingleFactor] = None

  /**Allows for comparing the age between different messages and finding the message that was updated least recently.*/
  private var msgIndex: Long = -1

  def setEndGate(gate: END_GATE) { endGate = Some(gate) }
  def getEndGate(): END_GATE = endGate.get

  def setMessage(newMessage: SingleFactor, msgIndex: Long) {
    message match {
      case None => {
        oldMessage = Some(newMessage)
        message = Some(newMessage)
      }
      case Some(msg) => {
        oldMessage = message
        message = Some(newMessage)
      }
    }
    this.msgIndex = msgIndex
  }

  def getMsgIndex(): Long = msgIndex
  def getMessage(): SingleFactor = message.get
  def getOldMessage(): SingleFactor = oldMessage.get
}

case class FactorGate(factorNode: FactorNode) extends Gate {
  type END_GATE = VarGate
}
case class VarGate(varNode: VarNode) extends Gate {
  type END_GATE = FactorGate
}