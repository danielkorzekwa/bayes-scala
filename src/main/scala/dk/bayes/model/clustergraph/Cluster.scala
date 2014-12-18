package dk.bayes.model.clustergraph

import factor._

/**
 * Represents cluster in a cluster graph.
 *
 * @author Daniel Korzekwa
 *
 * @param id Unique cluster id
 *
 * @param typeId Unique id of a cluster type.
 * Clusters of the same cluster type usually share the same cluster initial potentials. TypeId can be used for learning
 * parameters in dynamic bayesian networks, with multiple prior, transition and emission parameters.
 *
 * @param factor Initial cluster potentials
 */
class Cluster(val id: Int, val typeId: Int, factor: Factor) {

  private var _factor: Factor = factor
  private var edges: List[Edge] = List()

  def addEdge(edge: Edge) { edges = edge :: edges }

  def getEdges(): Seq[Edge] = edges

  def getFactor(): Factor = _factor

  def updateFactor(newFactor: Factor) {
    _factor = newFactor

    resetMessages()
  }
  
  def resetMessages() {
    edges.foreach { edge => edge.resetMessage() }
  }
}

object Cluster {

  /**
   * Creates cluster.
   *
   * @param id Unique cluster id
   *
   * @param factor Initial cluster potentials
   */
  def apply(id: Int, factor: Factor): Cluster = new Cluster(id, id, factor)

  /**
   * Creates cluster.
   *
   * @param id Unique cluster id
   *
   * @param typeId Unique id of a cluster type.
   * Clusters of the same cluster type usually share the same cluster initial potentials. TypeId can be used for learning
   * parameters in dynamic bayesian networks, with multiple prior, transition and emission parameters.
   *
   * @param factor Initial cluster potentials
   */
  def apply(id: Int, typeId: Int, factor: Factor): Cluster = new Cluster(id, typeId, factor)
}