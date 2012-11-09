package dk.bayes.em
import dk.bayes.clustergraph.ClusterGraph
import dk.bayes.infer.LoopyBP
import dk.bayes.factor.Factor
import dk.bayes.clustergraph.Cluster
import scala.collection._
import scala.collection.mutable.ListBuffer
import scala.annotation.tailrec

/**
 * Default implementation of EM algorithm.
 *
 * @author Daniel Korzekwa
 */
object GenericEMLearn extends EMLearn {

  def learn(clusterGraph: ClusterGraph, trainSet: DataSet, maxIterNum: Int, iterNum: (Int) => Unit = (iterNum: Int) => {}) = {

    /**
     * Returns learned cluster potentials by cluster type id.
     */
    @tailrec
    def trainRecursive(clusterPotentialsByTypeId: Map[Int, Factor], currIter: Int): Map[Int, Factor] = {
      iterNum(currIter)

      val clusterBeliefs = expectationStep(clusterGraph, clusterPotentialsByTypeId, trainSet)
      val newClusterPotentialsByTypeId = maximisationStep(clusterBeliefs)

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
   *
   * @return Seq[Tuple2[clusterTypeId,cluster belief]]
   */
  private def expectationStep(clusterGraph: ClusterGraph, clusterPotentialsByTypeId: Map[Int, Factor], trainSet: DataSet): Seq[Tuple2[Int, Factor]] = {

    val loopyBP = LoopyBP(clusterGraph)

    val clusterBeliefs: Seq[Tuple2[Int, Factor]] = trainSet.samples.flatMap { sample =>

      updateInitialClusterPotentials(clusterGraph, clusterPotentialsByTypeId)

      var i = 0
      while (i < trainSet.variableIds.size) {
        val varValue = sample(i)
        if (varValue >= 0) loopyBP.setEvidence(trainSet.variableIds(i), varValue)
        i += 1
      }

      loopyBP.calibrate()

      /**Seq[Tuple2[clusterTypeId,cluster belief]]*/
      val clusterBeliefs: Seq[Tuple2[Int, Factor]] = clusterGraph.getClusters().map(c => c.typeId -> loopyBP.clusterBelief(c.id))
      clusterBeliefs
    }

    clusterBeliefs
  }

  /**
   * Returns cluster initial potentials estimated from sufficient statistics using MLE (Maximum Likelihood Estimation).
   *
   * @returns Map[clusterTypeId, MLE estimated cluster initial potentials]
   */
  private def maximisationStep(clusterBeliefs: Seq[Tuple2[Int, Factor]]): Map[Int, Factor] = {

    val clusterBeliefsByTypeId: Map[Int, Seq[Factor]] = clusterBeliefs.groupBy(c => c._1).mapValues(v => v.map(_._2))
    clusterBeliefsByTypeId

    val clusterPotentialsByTypeId: Map[Int, Factor] = clusterBeliefsByTypeId.map {
      case (clusterTypeId, clusterBeliefs) =>

        val clusterBeliefFactor = clusterBeliefs.head
        val beliefValuesSum = new Array[Double](clusterBeliefFactor.getValues().size)

        for (belief <- clusterBeliefs) {
          val beliefValues = belief.getValues()
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
  def toCPT(values: Seq[Double], sliceSize: Int): Array[Double] = {

    val cptValues = values.isEmpty match {
      case true => Nil
      case false => values.grouped(sliceSize).flatMap { slice =>
        slice.map(elem => elem / slice.sum)
      }
    }

    cptValues.toArray
  }

}