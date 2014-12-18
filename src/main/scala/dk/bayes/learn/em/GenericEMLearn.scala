package dk.bayes.learn.em
import dk.bayes.model.clustergraph.ClusterGraph
import dk.bayes.infer.LoopyBP
import dk.bayes.model.clustergraph.factor.Factor
import scala.collection._
import scala.annotation.tailrec
import EMLearn._

/**
 * Default implementation of EM algorithm.
 *
 * @author Daniel Korzekwa
 */
object GenericEMLearn extends EMLearn {

  private val ZERO_PROBABILITY = 1.0E-20

  /**
   * Represents sufficient statistics produced by E-step of EM algorithm.
   */
  case class SufficientStats(clusterBeliefsByTypeId: Seq[Tuple2[Int, Factor]], logLikelihood: Double)

  def learn(clusterGraph: ClusterGraph, trainSet: DataSet, maxIterNum: Int, progress: (Progress) => Unit = (progress: Progress) => {}) = {

    require(trainSet.samples.size > 0, "No samples found in training set")

    /**
     * Returns learned cluster potentials by cluster type id.
     */
    @tailrec
    def trainRecursive(clusterPotentialsByTypeId: Map[Int, Factor], currIter: Int): Map[Int, Factor] = {

      val sufficientStats = expectationStep(clusterGraph, clusterPotentialsByTypeId, trainSet)
      val newClusterPotentialsByTypeId = maximisationStep(sufficientStats.clusterBeliefsByTypeId)

      progress(Progress(currIter, sufficientStats.logLikelihood))

      if (currIter < maxIterNum) trainRecursive(newClusterPotentialsByTypeId, currIter + 1)
      else newClusterPotentialsByTypeId
    }

    val loopyBP = LoopyBP(clusterGraph)
    /**Seq[Tuple2[clusterTypeId,cluster initial potentials]]*/
    val clusterPotentials = clusterGraph.getClusters().map(c => c.typeId -> loopyBP.clusterBelief(c.id))
    val clusterPotentialsByTypeId: Map[Int, Factor] = maximisationStep(clusterPotentials)

    val finalClusterPotentialsByTypeId = trainRecursive(clusterPotentialsByTypeId, 1)

    updateInitialClusterPotentials(clusterGraph, finalClusterPotentialsByTypeId)

  }

  private def updateInitialClusterPotentials(clusterGraph: ClusterGraph, clusterPotentialsByTypeId: Map[Int, Factor]) {
    for (cluster <- clusterGraph.getClusters()) {
      val clusterTypePotentials = clusterPotentialsByTypeId(cluster.typeId)
      val newClusterPotentials = cluster.getFactor().copy(clusterTypePotentials.getValues())
      cluster.updateFactor(newClusterPotentials)
    }
  }

  /**
   * Returns sufficient statistics.
   */
  private def expectationStep(clusterGraph: ClusterGraph, clusterPotentialsByTypeId: Map[Int, Factor], trainSet: DataSet): SufficientStats = {

    val loopyBP = LoopyBP(clusterGraph)

    var dataLogLikelihood = 0d

    val clusterBeliefs: Seq[Tuple2[Int, Factor]] = trainSet.samples.flatMap { sample =>

      updateInitialClusterPotentials(clusterGraph, clusterPotentialsByTypeId)

      val evidence: Seq[Tuple2[Int, Int]] = DataSet.toEvidence(trainSet.variableIds, sample)

      val logLikelihood = loopyBP.calibrateWithEvidence(evidence)
      dataLogLikelihood += logLikelihood

      /**Seq[Tuple2[clusterTypeId,cluster belief]]*/
      val clusterBeliefs: Seq[Tuple2[Int, Factor]] = clusterGraph.getClusters().map(c => c.typeId -> loopyBP.clusterBelief(c.id))
      clusterBeliefs
    }

    SufficientStats(clusterBeliefs, dataLogLikelihood)
  }

  /**
   * Returns cluster initial potentials estimated from sufficient statistics using MLE (Maximum Likelihood Estimation).
   *
   * @returns Map[clusterTypeId, MLE estimated cluster initial potentials]
   */
  private def maximisationStep(clusterBeliefs: Seq[Tuple2[Int, Factor]]): Map[Int, Factor] = {

    val clusterBeliefsByTypeId: Map[Int, Seq[Factor]] = clusterBeliefs.groupBy(c => c._1).mapValues(v => v.map(_._2))

    val clusterPotentialsByTypeId: Map[Int, Factor] = clusterBeliefsByTypeId.map {
      case (clusterTypeId, clusterBeliefs) =>

        val clusterBeliefFactor = clusterBeliefs.head
        val beliefValuesSum = new Array[Double](clusterBeliefFactor.getValues().size)

        for (belief <- clusterBeliefs) {
          val beliefValues = belief.getValues().map(v => if (v == 0) ZERO_PROBABILITY else v)
          var i = 0
          while (i < beliefValues.size) {
            beliefValuesSum(i) += beliefValues(i)
            i += 1
          }
        }

        val cptVarSize = clusterBeliefFactor.getVariables().last.dim
        val cptValues = toCPT(beliefValuesSum, cptVarSize)
        val clusterPotentials = clusterBeliefFactor.copy(cptValues)

        (clusterTypeId -> clusterPotentials)
    }

    clusterPotentialsByTypeId
  }

  /**
   * Converts factor values to CPT values.
   */
  private def toCPT(values: Seq[Double], sliceSize: Int): Array[Double] = {

    val cptValues = values.isEmpty match {
      case true => Nil
      case false => values.grouped(sliceSize).flatMap { slice =>
      slice.map(elem => elem / slice.sum)
    }
    }

    cptValues.toArray
  }

}