package dk.bayes.learn.em

import org.junit._
import Assert._
import dk.bayes.model.clustergraph.factor._
import dk.bayes.model.clustergraph._
import dk.bayes.testutil.AssertUtil._
import dk.bayes.testutil.TennisDBN._
import dk.bayes.infer.LoopyBP
import EMLearn._

class EMLearnTennisGettingStarted {

  def progress(progress: Progress) = println("EM progress(iterNum, logLikelihood): " + progress.iterNum + ", " + progress.logLikelihood)
  
  @Test def test {

    val tennisClusterGraph = createTennisClusterGraph()

    //Prepare training set
    val variableIds = Array(
      player1Time0Var.id, player1Time1Var.id, player1Time2Var.id,
      player2Time0Var.id, player2Time1Var.id, player2Time2Var.id,
      player3Time1Var.id, player3Time2Var.id,
      match1v2Time0Var.id, match1v2Time1Var.id, match2v3Time1Var.id, match1v2Time2Var.id, match1v3Time2Var.id, match2v3Time2Var.id)
    val dataSet = DataSet.fromFile("src/test/resources/tennis_data/tennis_3_players_network.dat", variableIds)

    //Learn parameters
    val maxIterNum = 5
    GenericEMLearn.learn(tennisClusterGraph, dataSet, maxIterNum,progress)

    val expectedPriorParameter = Factor(player1Time0Var, Array(0.4729, 0.2323, 0.2947))

    val expectedTransitionParameter = Factor(player2Time0Var, player2Time1Var,
      Array(0.9998, 0.0001, 0.0001, 0.0083, 0.9720, 0.0197, 0.0020, 0.0091, 0.9890))

    val expectedEmissionParameter = Factor(player1Time2Var, player2Time2Var, match1v2Time2Var,
      Array(0.0000, 1.0000, 0.0000, 1.0000, 0.0000, 1.0000, 0.9930, 0.0070, 0.9198, 0.0802, 0.8337, 0.1663, 0.9980, 0.0020, 0.9956, 0.0044, 0.9960, 0.0040))

    assertFactor(expectedPriorParameter, tennisClusterGraph.getCluster(player1Time0Var.id).getFactor(), 0.0001)
    assertFactor(expectedTransitionParameter, tennisClusterGraph.getCluster(player2Time1Var.id).getFactor(), 0.0001)
    assertFactor(expectedEmissionParameter, tennisClusterGraph.getCluster(match1v2Time2Var.id).getFactor(), 0.0001)
  }

}