package dk.bayes.clustergraph

import dk.bayes.factor.Factor
import ClusterGraph._
import dk.bayes.factor.SingleFactor

/**
 * Represents Bayesian Network (or more generally Probabilistic Graphical Model) as a cluster graph.
 * 'Daphne Koller, Nir Friedman. Probabilistic Graphical Models, Principles and Techniques, 2009'
 *
 * @author Daniel Korzekwa
 */
trait ClusterGraph {

  /**
   * Adds cluster to this cluster graph.
   *
   * @param clusterId Unique cluster id
   * @param factor Initial cluster potentials
   */
  def addCluster(clusterId: Int, factor: Factor)

  /**
   * Adds edge between clusters in this cluster graph.
   */
  def addEdge(clusterId1: Int, clusterId2: Int)

  /**
   * Adds edges between clusters in this cluster graph.
   *
   * @param firstEdge Tuple2[clusterId, clusterId2]
   * @param nextEdges Tuple2[clusterId, clusterId2]
   */
  def addEdges(firstEdge: Tuple2[Int, Int], nextEdges: Tuple2[Int, Int]*)

  /**
   * Returns all clusters in this cluster graph.
   */
  def getClusters(): Seq[Cluster]

  /**
   * Returns cluster for a cluster id.
   */
  def getCluster(clusterId:Int):Cluster
}

object ClusterGraph {

  /**
   * Creates default cluster graph.
   */
  def apply(): ClusterGraph = GenericClusterGraph()
}