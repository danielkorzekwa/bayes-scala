package dk.bayes.infer
import dk.bayes.clustergraph.ClusterGraph
import scala.annotation.tailrec
import scala.util.Random
import dk.bayes.clustergraph.ClusterGraph._
import dk.bayes.clustergraph.Cluster
import dk.bayes.clustergraph.Edge
import dk.bayes.factor.Factor
import dk.bayes.factor.SingleFactor
import scala.Math._

/**
 * Loopy BP calibration and inference in a cluster graph, presented in
 * 'Daphne Koller, Nir Friedman. Probabilistic Graphical Models, Principles and Techniques, 2009' book.
 *
 *  @author Daniel Korzekwa
 *  
 *  @param clusterGraph  
 *  @param threshold Maximum absolute difference between old and new corresponding messages in a cluster graph,
 *  before calibration is completed
 */
case class LoopyBP(clusterGraph: ClusterGraph, threshold: Double = 0.00001) extends ClusterGraphInfer {

  def calibrate(iterNum: (Int) => Unit = (iterNum: Int) => {}) {

    val rand = new Random(System.currentTimeMillis())

    @tailrec
    def calibrateUntilConverge(currentIter: Int): ClusterGraph = {

      iterNum(currentIter)

      val shuffledClusters = rand.shuffle(clusterGraph.getClusters())
      shuffledClusters.foreach(c => calibrateCluster(c))

      if (isCalibrated(clusterGraph)) clusterGraph else calibrateUntilConverge(currentIter + 1)
    }

    calibrateUntilConverge(1)
  }

  private def calibrateCluster(cluster: Cluster) {

    cluster.getEdges().foreach { edge =>

      var newMessageFactor = cluster.getFactor()
      for (edgeIn <- cluster.getEdges()) {
        if (edgeIn.destClusterId != edge.destClusterId) {
          newMessageFactor = newMessageFactor.product(edgeIn.getIncomingEdge().get.getNewMessage())
        }
      }

      val varId = edge.getNewMessage().getVariable().id
      val newMessage = newMessageFactor.marginal(varId).normalise()
      edge.updateMessage(newMessage)
    }

  }

  private def isCalibrated(clusterGraph: ClusterGraph): Boolean = {

    val notCalibratedCluster = clusterGraph.getClusters().find { cluster =>

      //Find not calibrated cluster edge
      cluster.getEdges().find { edge =>

        edge.getOldMessage().getValues().zip(edge.getNewMessage().getValues()).
          find {
            case (oldMsgVal, newMsgVal) => abs(oldMsgVal - newMsgVal) > threshold
          }.isDefined

      }.isDefined

    }

    !notCalibratedCluster.isDefined
  }

  def clusterBelief(clusterId: Int): Factor = {
    val cluster = clusterGraph.getClusters().find(c => c.id == clusterId).get
    val messagesIn = cluster.getEdges().map(e => e.getIncomingEdge().get.getNewMessage())

    val clusterBelief = messagesIn.foldLeft(cluster.getFactor())((factorProduct, factor) => factorProduct.product(factor))
    clusterBelief.normalise()
  }

  def logLikelihood(assignment: Array[Tuple2[Int, Int]]): Double = {
    val allVariableIds = clusterGraph.getClusters().flatMap(c => c.getFactor().getVariables().map(v => v.id)).distinct

    val assignmentDiff = allVariableIds.diff(assignment.map(a => a._1))
    require(assignmentDiff.size == 0, "Assignment of all variables in a cluster is required")
    require(assignment.size == assignment.distinct.size, "Assignment is not unique")

    val clustersLoglikelihood = clusterGraph.getClusters().map(c => log(likelihood(clusterBelief(c.id), assignment))).sum

    val sepsetBeliefs: Seq[Factor] = clusterGraph.getClusters().flatMap { c =>
      val edges = c.getEdges().filter(e => c.id > e.destClusterId)
      val sepsetBeliefs = edges.map(e => e.getIncomingEdge().get.getNewMessage().product(e.getNewMessage()).normalise())
      sepsetBeliefs
    }

    val sepsetLoglikelihood = sepsetBeliefs.map(belief => log(likelihood(belief, assignment))).sum

    clustersLoglikelihood - sepsetLoglikelihood
  }

  private def likelihood(factor: Factor, assignment: Seq[Tuple2[Int, Int]]): Double = {

    def assignmentValue(variableId: Int): Int = assignment.find(a => a._1 == variableId).get._2

    val factorAssignment: Array[Int] = factor.getVariables().map(v => assignmentValue(v.id))
    factor.getValue(factorAssignment)
  }

  def marginal(variableId: Int): Factor = {
    val varCluster = clusterGraph.getClusters().find(c => c.getFactor().getVariables().map(v => v.id).contains(variableId)).get
    val marginalFactor = clusterBelief(varCluster.id).marginal(variableId)
    marginalFactor
  }

  def setEvidence(evidence: Tuple2[Int, Int]) {
    val evidenceClusters = clusterGraph.getClusters().filter(c => c.getFactor().getVariables().map(v => v.id).contains(evidence._1))

    evidenceClusters.foreach(c => c.updateFactor(c.getFactor().withEvidence(evidence)))
  }
}