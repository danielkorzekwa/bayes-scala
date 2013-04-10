package dk.bayes.model.factorgraph

import dk.bayes.model.factor.Factor

/**
 * This class represents an outgoing gate from a factor/variable node in a factor graph.
 *
 * @author Daniel Korzekwa
 *
 * @param message The initial outgoing message sent through the gate
 */
case class Gate(message: Factor) {

  private var endGate: Option[Gate] = None

  def setEndGate(gate: Gate) { endGate = Some(gate) }
  def getEndGate(): Gate = endGate.get
}