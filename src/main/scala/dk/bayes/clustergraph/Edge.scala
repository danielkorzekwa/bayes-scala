package dk.bayes.clustergraph
import dk.bayes.factor.SingleFactor

/**
 * Represents outgoing edge in a cluster graph
 *
 * @author Daniel Korzekwa
 *
 * @param destClusterId Destination cluster id for this edge
 * @param initialMessage Initial outgoing message
 */
class Edge(val destClusterId: Int, initialMessage: SingleFactor) {

  private var incomingEdge: Option[Edge] = None

  private var oldMessage: SingleFactor = initialMessage
  private var newMessage: SingleFactor = initialMessage

  def setIncomingEdge(edge: Edge) {
    incomingEdge = Some(edge)
  }
  def getIncomingEdge(): Option[Edge] = incomingEdge

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
   * @param initialMessage Initial outgoing message
   */
  def apply(destClusterId: Int, initialMessage: SingleFactor): Edge = new Edge(destClusterId, initialMessage)
}