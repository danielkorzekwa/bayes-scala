package dk.bayes.model.clustergraph

import factor._
import ClusterGraph._
import scala.collection._
import scala.annotation.tailrec
import scala.util.Random


/**
 * Default implementation of a ClusterGraph.
 *
 * @author Daniel Korzekwa
 */
case class GenericClusterGraph() extends ClusterGraph {

  private var clusters: List[Cluster] = List()

  def getClusters(): Seq[Cluster] = clusters

  def addCluster(clusterId: Int, factor: Factor, clusterTypeId: Option[Int] = None) = {
    val cluster = if (clusterTypeId.isEmpty) Cluster(clusterId, factor)
    else Cluster(clusterId, clusterTypeId.get, factor)
    
    clusters = cluster :: clusters
  }

  def getCluster(clusterId: Int): Cluster = clusters.find(c => c.id == clusterId).get

  def addEdge(clusterId1: Int, clusterId2: Int) {
    val cluster1 = clusters.find(c => c.id == clusterId1).get
    val cluster2 = clusters.find(c => c.id == clusterId2).get
    val sepsetVariable = calcSepsetVariable(cluster1, cluster2)

    val edge12 = Edge(clusterId2, sepsetVariable)
    val edge21 = Edge(clusterId1, sepsetVariable)

    edge12.setIncomingEdge(edge21)
    edge21.setIncomingEdge(edge12)

    cluster1.addEdge(edge12)
    cluster2.addEdge(edge21)
  }

  def addEdges(firstEdge: Tuple2[Int, Int], nextEdges: Tuple2[Int, Int]*) {
    addEdge(firstEdge._1, firstEdge._2)
    nextEdges.foreach(e => addEdge(e._1, e._2))
  }
  
  def getVariables():Seq[Var] = clusters.flatMap(c => c.getFactor().getVariables()).distinct

  private def calcSepsetVariable(cluster1: Cluster, cluster2: Cluster): Var = {
    val intersectVariables = cluster1.getFactor().getVariables().intersect(cluster2.getFactor().getVariables())
    require(intersectVariables.size == 1, "Sepset must contain single variable only")
    val intersectVariable = intersectVariables.head

    intersectVariable
  }

}