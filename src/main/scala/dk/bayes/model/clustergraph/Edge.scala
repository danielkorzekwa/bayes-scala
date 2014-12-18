package dk.bayes.model.clustergraph

import factor._

/**
 * Represents outgoing edge in a cluster graph
 *
 * @author Daniel Korzekwa
 *
 * @param destClusterId Destination cluster id for this edge
 * @param sepsetVariable Shared variable between clusters for this edge
 */
class Edge(val destClusterId: Int, val sepsetVariable: Var) {

  private var incomingEdge: Option[Edge] = None

  private var newMessage: SingleFactor = SingleFactor(sepsetVariable, Array.fill(sepsetVariable.dim)(1d))
  private var oldMessage: SingleFactor = newMessage

  def setIncomingEdge(edge: Edge) {
    incomingEdge = Some(edge)
  }
  def getIncomingEdge(): Option[Edge] = incomingEdge

  def resetMessage() {
    newMessage = SingleFactor(sepsetVariable, Array.fill(sepsetVariable.dim)(1d))
    oldMessage = newMessage
  }

  def updateMessage(message: SingleFactor) {
    oldMessage = newMessage
    newMessage = message
  }

  def getOldMessage(): SingleFactor = oldMessage
  def getNewMessage(): SingleFactor = newMessage
}

object Edge {

  /**
   * Creates outgoing edge in a cluster graph.
   *
   * @param destClusterId Destination cluster id for this edge
   * @param sepsetVariable Shared variable between clusters for this edge
   */
  def apply(destClusterId: Int, sepsetVariable: Var): Edge = new Edge(destClusterId, sepsetVariable)
}