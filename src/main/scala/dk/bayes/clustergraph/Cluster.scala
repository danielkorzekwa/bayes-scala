package dk.bayes.clustergraph

import dk.bayes.factor._

/**
 * Represents cluster in a cluster graph.
 *
 * @author Daniel Korzekwa
 *
 * @param id Unique cluster id
 * @param factor Initial cluster potentials
 */
class Cluster(val id: Int, factor: Factor) {

  private var _factor: Factor = factor
  private var edges: List[Edge] = List()

  def addEdge(edge: Edge) { edges = edge :: edges }

  def getEdges(): Seq[Edge] = edges

  def getFactor(): Factor = _factor

  def updateFactor(newFactor: Factor) { _factor = newFactor }
}

object Cluster {

  /**
   * Creates cluster.
   *
   * @param id Unique cluster id
   * @param factor Initial cluster potentials
   */
  def apply(id: Int, factor: Factor): Cluster = new Cluster(id, factor)
}