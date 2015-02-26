package dk.bayes.infer
import dk.bayes.model.clustergraph.ClusterGraph
import scala.annotation.tailrec
import scala.util.Random
import dk.bayes.model.clustergraph.ClusterGraph._
import dk.bayes.model.clustergraph.Cluster
import dk.bayes.model.clustergraph.Edge
import dk.bayes.model.clustergraph.factor._
import scala.math._
import LoopyBP._

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

  def calibrate(iterNum: (Int) => Unit = (iterNum: Int) => {}, messageOrder: MessageOrder = ForwardBackwardMsgOrder()) {

    @tailrec
    def calibrateUntilConverge(currentIter: Int): ClusterGraph = {

      iterNum(currentIter)

      val orderedClusters = messageOrder.ordered(clusterGraph.getClusters())
      orderedClusters.foreach(c => calibrateCluster(c))

      if (isCalibrated(clusterGraph)) clusterGraph else calibrateUntilConverge(currentIter + 1)
    }

    calibrateUntilConverge(1)
  }

  def calibrateWithEvidence(evidence: Seq[Tuple2[Int, Int]], iterNum: (Int) => Unit = (iterNum: Int) => {}): Double = {

    val evidenceByVariableId = Map(evidence: _*)
    val allVariables = clusterGraph.getVariables()

    //Array of Tuple2[varId,varValue] Fix all variables inconsistent with evidence to 0 (first variable value)
    val assignment: Array[Tuple2[Int, Int]] = allVariables.map(v => v.id -> evidenceByVariableId.getOrElse(v.id, 0)).toArray

    val jointProb = logLikelihood(assignment)

    evidence.foreach(e => setEvidence(e))
    calibrate(iterNum)

    val conditionalProb = logLikelihood(assignment)

    //Following conditional probability formula
    val evidenceLogLikelihood = jointProb - conditionalProb

    evidenceLogLikelihood
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
    val allVariableIds = clusterGraph.getVariables().map(v => v.id)

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

    for (cluster <- clusterGraph.getClusters()) {

      val variables = cluster.getFactor().getVariables()
      var i = 0
      var continue = true
      while (continue && i < variables.size) {
        if (evidence._1 == variables(i).id) {
          val newFactor = cluster.getFactor().withEvidence(evidence)
          require(newFactor.getValues().sum > 0, "All factor values can't be set to zero. Cluster id: " + cluster.id)
          cluster.updateFactor(newFactor)
          continue = false
        }
        i += 1
      }
    }
  }

}

object LoopyBP {
  case class ForwardMsgOrder() extends MessageOrder {
    def ordered(clusters: Seq[Cluster]): Seq[Cluster] = clusters
  }

  case class ForwardBackwardMsgOrder() extends MessageOrder {
    def ordered(clusters: Seq[Cluster]): Seq[Cluster] = clusters ++ clusters.reverse
  }

}