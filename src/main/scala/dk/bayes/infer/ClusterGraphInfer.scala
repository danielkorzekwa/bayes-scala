package dk.bayes.infer
import dk.bayes.model.clustergraph.ClusterGraph
import dk.bayes.model.clustergraph._
import dk.bayes.model.clustergraph.factor.Factor

/**
 * Provides inference functionality in a cluster graph.
 *
 * @author Daniel Korzekwa
 *
 */
trait ClusterGraphInfer {

  /**
   * Calibrates cluster graph.
   *
   * @param iterNum Progress monitoring. It is called by this method at the beginning of every iteration
   * @param messageOrder Order of clusters in which messages are sent for a single iteration of Belief Propagation
   *
   */
  def calibrate(iterNum: (Int) => Unit, messageOrder: MessageOrder)

  /**
   * Applies evidence and calibrates cluster graph.
   *
   * @param evidence Sequence of Tuple2[variableId, variable value]
   * @param iterNum Progress monitoring. It is called by this method at the beginning of every iteration
   *
   * @return Log likelihood of evidence in a cluster graph.
   */
  def calibrateWithEvidence(evidence: Seq[Tuple2[Int, Int]], iterNum: (Int) => Unit): Double

  /**
   * Returns cluster belief.
   *
   * @param clusterId Unique cluster id
   *
   */
  def clusterBelief(clusterId: Int): Factor

  /**
   * Returns log likelihood of assignment for all variables in a cluster graph.
   *
   * @param assignment Array of Tuple2[variableId, variable value]
   */
  def logLikelihood(assignment: Array[Tuple2[Int, Int]]): Double

  /**
   * Returns marginal factor for a variable in a cluster graph.
   */
  def marginal(variableId: Int): Factor

  /**
   * Sets evidence in a cluster graph. Once evidence is set in a cluster graph, it cannot be reverted.
   *
   *  @param evidence Tuple2[variableId, variable value]
   */
  def setEvidence(evidence: Tuple2[Int, Int])
}