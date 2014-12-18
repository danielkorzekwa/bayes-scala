package dk.bayes.infer

import org.junit._
import Assert._
import dk.bayes.testutil.StudentBN
import dk.bayes.model.clustergraph.GenericClusterGraph
import dk.bayes.testutil._
import dk.bayes.testutil.AssertUtil._
import dk.bayes.model.clustergraph.factor._
import dk.bayes.model.clustergraph.ClusterGraph
import SprinklerBN._
import Double._

class LoopyBPSprinklerTest {

  val sprinklerGraph = createSprinklerGraph()

  def progress(iterNum: Int) = println("Loopy BP iter= " + iterNum)

  val loopyBP = LoopyBP(sprinklerGraph)

  @Test(expected = classOf[IllegalArgumentException]) def cluster_belief_given_full_evidence_with_zero_probability {
    val wetGrassFactorWithZeroProbability = Factor(sprinklerVar, rainVar, wetGrassVar, Array(0.95, 0.05, 0.9, 0.1, 0.8, 0.2, 0, 1))
    sprinklerGraph.getCluster(wetGrassVar.id).updateFactor(wetGrassFactorWithZeroProbability)

    loopyBP.setEvidence(winterVar.id, 0)
    loopyBP.setEvidence(sprinklerVar.id, 1)
    loopyBP.setEvidence(rainVar.id, 1)
    loopyBP.setEvidence(wetGrassVar.id, 0)
  }

  @Test(expected = classOf[IllegalArgumentException]) def incompatible_evidence {
    loopyBP.setEvidence(winterVar.id, 0)
    loopyBP.setEvidence(winterVar.id, 1)
  }

  /**
   * This test checks, whether cluster messages are reset to 1 at the beginning of loopy belief calibration.
   * If they weren't then cluster beliefs would equal to NaN.
   */
  @Test def cluster_belief_calibrate_twice_for_two_different_full_assignments {
    loopyBP.setEvidence(winterVar.id, 0)
    loopyBP.setEvidence(sprinklerVar.id, 1)
    loopyBP.setEvidence(rainVar.id, 0)
    loopyBP.setEvidence(wetGrassVar.id, 0)
    loopyBP.setEvidence(slipperyRoadVar.id, 0)

    loopyBP.calibrate(progress)

    //Reset evidence
    sprinklerGraph.getCluster(winterVar.id).updateFactor(winterFactor)
    sprinklerGraph.getCluster(sprinklerVar.id).updateFactor(sprinklerFactor)
    sprinklerGraph.getCluster(rainVar.id).updateFactor(rainFactor)
    sprinklerGraph.getCluster(wetGrassVar.id).updateFactor(wetGrassFactor)
    sprinklerGraph.getCluster(slipperyRoadVar.id).updateFactor(slipperyRoadFactor)

    loopyBP.setEvidence(winterVar.id, 0)
    loopyBP.setEvidence(sprinklerVar.id, 1)
    loopyBP.setEvidence(rainVar.id, 1)
    loopyBP.setEvidence(wetGrassVar.id, 0)
    loopyBP.setEvidence(slipperyRoadVar.id, 1)

    loopyBP.calibrate(progress)

    val winterClusterBelief = loopyBP.clusterBelief(winterVar.id)
    val sprinklerClusterBelief = loopyBP.clusterBelief(sprinklerVar.id)
    val rainClusterBelief = loopyBP.clusterBelief(rainVar.id)
    val wetGrassClusterBelief = loopyBP.clusterBelief(wetGrassVar.id)
    val slipperyClusterBelief = loopyBP.clusterBelief(slipperyRoadVar.id)

    assertFactor(Factor(winterVar, Array(1d, 0)), winterClusterBelief, 0.0001)
    assertFactor(Factor(winterVar, sprinklerVar, Array(0d, 1, 0, 0)), sprinklerClusterBelief, 0.0001)

    assertFactor(Factor(winterVar, rainVar, Array(0, 1, 0, 0)), rainClusterBelief, 0.0001)

    assertFactor(Factor(sprinklerVar, rainVar, wetGrassVar, Array(0, 0, 0, 0, 0, 0, 1, 0)), wetGrassClusterBelief, 0.0001)
    assertFactor(Factor(rainVar, slipperyRoadVar, Array(0, 0, 0, 1)), slipperyClusterBelief, 0.0001)
  }
}