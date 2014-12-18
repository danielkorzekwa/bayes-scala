package dk.bayes.learn.em

import org.junit._
import Assert._
import dk.bayes.model.clustergraph.factor._
import dk.bayes.model.clustergraph._
import dk.bayes.testutil.AssertUtil._
import EMLearn._

class EMLearnSprinklerGettingStarted {

  def progress(progress: Progress) = println("EM progress(iterNum, logLikelihood): " + progress.iterNum + ", " + progress.logLikelihood)
  
  @Test def test {

    //Create variables
    val winterVar = Var(1, 2)
    val sprinklerVar = Var(2, 2)
    val rainVar = Var(3, 2)
    val wetGrassVar = Var(4, 2)
    val slipperyRoadVar = Var(5, 2)

    //Create factors
    val winterFactor = Factor(winterVar, Array(0.2, 0.8))
    val sprinklerFactor = Factor(winterVar, sprinklerVar, Array(0.6, 0.4, 0.55, 0.45))
    val rainFactor = Factor(winterVar, rainVar, Array(0.1, 0.9, 0.3, 0.7))
    val wetGrassFactor = Factor(sprinklerVar, rainVar, wetGrassVar, Array(0.85, 0.15, 0.3, 0.7, 0.35, 0.65, 0.55, 0.45))
    val slipperyRoadFactor = Factor(rainVar, slipperyRoadVar, Array(0.5, 0.5, 0.4, 0.6))

    //Create cluster graph	
    val sprinklerGraph = ClusterGraph()

    sprinklerGraph.addCluster(winterVar.id, winterFactor)
    sprinklerGraph.addCluster(sprinklerVar.id, sprinklerFactor)
    sprinklerGraph.addCluster(rainVar.id, rainFactor)
    sprinklerGraph.addCluster(wetGrassVar.id, wetGrassFactor)
    sprinklerGraph.addCluster(slipperyRoadVar.id, slipperyRoadFactor)

    sprinklerGraph.addEdges((1, 2), (1, 3), (2, 4), (3, 4), (3, 5))

    //Learn parameters
    val maxIterNum = 5
    val variableIds = Array(winterVar.id, rainVar.id, sprinklerVar.id, slipperyRoadVar.id, wetGrassVar.id)
    val dataSet = DataSet.fromFile("src/test/resources/sprinkler_data/sprinkler_10k_samples_5pct_missing_values.dat", variableIds)

    GenericEMLearn.learn(sprinklerGraph, dataSet, maxIterNum,progress)

    val expectedWinterFactor = sprinklerGraph.getCluster(winterVar.id).getFactor() //Factor(winterVar, Array(0.6086, 0.3914))
    val expectedSprinklerFactor = sprinklerGraph.getCluster(sprinklerVar.id).getFactor() //Factor(winterVar, sprinklerVar, Array(0.2041, 0.7958, 0.7506, 0.2493))
    val expectedRainFactor = sprinklerGraph.getCluster(rainVar.id).getFactor() //Factor(winterVar, rainVar, Array(0.8066, 0.1933, 0.0994, 0.9005))
    val expectedWetGrassFactor = sprinklerGraph.getCluster(wetGrassVar.id).getFactor() //Factor(sprinklerVar, rainVar, wetGrassVar, Array(0.9481, 0.0518, 0.9052, 0.0947, 0.7924, 0.2075, 0.00001, 0.9999))
    val expectedSlipperyRoadFactor = sprinklerGraph.getCluster(slipperyRoadVar.id).getFactor() //Factor(rainVar, slipperyRoadVar, Array(.6984, 0.3015, 0.00001, 0.9999))

    assertFactor(expectedWinterFactor, sprinklerGraph.getCluster(winterVar.id).getFactor(), 0.0001)
    assertFactor(expectedSprinklerFactor, sprinklerGraph.getCluster(sprinklerVar.id).getFactor(), 0.0001)
    assertFactor(expectedRainFactor, sprinklerGraph.getCluster(rainVar.id).getFactor(), 0.0001)
    assertFactor(expectedWetGrassFactor, sprinklerGraph.getCluster(wetGrassVar.id).getFactor(), 0.0001)
    assertFactor(expectedSlipperyRoadFactor, sprinklerGraph.getCluster(slipperyRoadVar.id).getFactor(), 0.0001)
  }
}