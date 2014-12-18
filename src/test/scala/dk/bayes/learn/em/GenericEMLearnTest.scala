package dk.bayes.learn.em

import org.junit._
import Assert._
import dk.bayes.testutil.SprinklerBN
import dk.bayes.testutil.SprinklerBN._
import dk.bayes.model.clustergraph.factor.Factor
import dk.bayes.testutil.AssertUtil._
import EMLearn._

class GenericEMLearnTest {

  val variableIds = Array(winterVar.id, rainVar.id, sprinklerVar.id, slipperyRoadVar.id, wetGrassVar.id)

  val maxIterNum = 5

  val sprinklerGraph = createSprinklerGraph()

  val initialWinterFactor = Factor(winterVar, Array(0.2, 0.8))
  val initialSprinklerFactor = Factor(winterVar, sprinklerVar, Array(0.6, 0.4, 0.55, 0.45))
  val initialRainFactor = Factor(winterVar, rainVar, Array(0.1, 0.9, 0.3, 0.7))
  val initialWetGrassFactor = Factor(sprinklerVar, rainVar, wetGrassVar, Array(0.85, 0.15, 0.3, 0.7, 0.35, 0.65, 0.55, 0.45))
  val initialSlipperyRoadFactor = Factor(rainVar, slipperyRoadVar, Array(0.5, 0.5, 0.4, 0.6))

  sprinklerGraph.getCluster(winterVar.id).updateFactor(initialWinterFactor)
  sprinklerGraph.getCluster(sprinklerVar.id).updateFactor(initialSprinklerFactor)
  sprinklerGraph.getCluster(rainVar.id).updateFactor(initialRainFactor)
  sprinklerGraph.getCluster(wetGrassVar.id).updateFactor(initialWetGrassFactor)
  sprinklerGraph.getCluster(slipperyRoadVar.id).updateFactor(initialSlipperyRoadFactor)

  def progress(progress: Progress) = println("EM progress(iterNum, logLikelihood): " + progress.iterNum + ", " + progress.logLikelihood)

  @Test(expected = classOf[IllegalArgumentException]) def train_no_samples {

    val dataSet = DataSet(variableIds, Array())

    GenericEMLearn.learn(sprinklerGraph, dataSet, maxIterNum, progress)
  }

  @Test def train_sprinkler_network_from_complete_data {

    val dataSet = DataSet.fromFile("src/test/resources/sprinkler_data/sprinkler_10k_samples_no_missing_values.dat", variableIds)

    GenericEMLearn.learn(sprinklerGraph, dataSet, maxIterNum, progress)

    val expectedWinterFactor = Factor(winterVar, Array(0.5929, 0.4071))
    val expectedSprinklerFactor = Factor(winterVar, sprinklerVar, Array(0.1983, 0.8016, 0.7550, 0.2449))
    val expectedRainFactor = Factor(winterVar, rainVar, Array(0.7967, 0.2032, 0.0901, 0.9098))
    val expectedWetGrassFactor = Factor(sprinklerVar, rainVar, wetGrassVar, Array(0.9634, 0.0365, 0.9001, 0.0998, 0.7895, 0.2104, 0, 1))
    val expectedSlipperyRoadFactor = Factor(rainVar, slipperyRoadVar, Array(0.6888, 0.3111, 0, 1))

    assertFactor(expectedWinterFactor, sprinklerGraph.getCluster(winterVar.id).getFactor(), 0.0001)
    assertFactor(expectedSprinklerFactor, sprinklerGraph.getCluster(sprinklerVar.id).getFactor(), 0.0001)
    assertFactor(expectedRainFactor, sprinklerGraph.getCluster(rainVar.id).getFactor(), 0.0001)
    assertFactor(expectedWetGrassFactor, sprinklerGraph.getCluster(wetGrassVar.id).getFactor(), 0.0001)
    assertFactor(expectedSlipperyRoadFactor, sprinklerGraph.getCluster(slipperyRoadVar.id).getFactor(), 0.0001)

  }

  @Test def train_sprinkler_network_from_incomplete_data {

    val dataSet = DataSet.fromFile("src/test/resources/sprinkler_data/sprinkler_10k_samples_5pct_missing_values.dat", variableIds)

    GenericEMLearn.learn(sprinklerGraph, dataSet, maxIterNum, progress)

    val expectedWinterFactor = Factor(winterVar, Array(0.6086, 0.3914))
    val expectedSprinklerFactor = Factor(winterVar, sprinklerVar, Array(0.2041, 0.7958, 0.7506, 0.2493))
    val expectedRainFactor = Factor(winterVar, rainVar, Array(0.8066, 0.1933, 0.0994, 0.9005))
    val expectedWetGrassFactor = Factor(sprinklerVar, rainVar, wetGrassVar, Array(0.9481, 0.0518, 0.9052, 0.0947, 0.7924, 0.2075, 0.00001, 0.9999))
    val expectedSlipperyRoadFactor = Factor(rainVar, slipperyRoadVar, Array(.6984, 0.3015, 0.00001, 0.9999))

    assertFactor(expectedWinterFactor, sprinklerGraph.getCluster(winterVar.id).getFactor(), 0.0001)
    assertFactor(expectedSprinklerFactor, sprinklerGraph.getCluster(sprinklerVar.id).getFactor(), 0.0001)
    assertFactor(expectedRainFactor, sprinklerGraph.getCluster(rainVar.id).getFactor(), 0.0001)
    assertFactor(expectedWetGrassFactor, sprinklerGraph.getCluster(wetGrassVar.id).getFactor(), 0.0001)
    assertFactor(expectedSlipperyRoadFactor, sprinklerGraph.getCluster(slipperyRoadVar.id).getFactor(), 0.0001)
  }
}