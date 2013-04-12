package dk.bayes.model.factorgraph

import dk.bayes.model.factor.Factor

/**
 * This class represents an outgoing gate from a factor/variable node in a factor graph.
 *
 * @author Daniel Korzekwa
 *
 * @param message The initial outgoing message sent through the gate
 */
case class Gate() {

  private var endGate: Option[Gate] = None

  private var message: Option[Factor] = None
  private var oldMessage: Option[Factor] = None

  def setEndGate(gate: Gate) { endGate = Some(gate) }
  def getEndGate(): Gate = endGate.get

  def setMessage(newMessage: Factor) {
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
  }

  def getMessage(): Factor = message.get
}