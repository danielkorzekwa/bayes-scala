package dk.bayes.testutil

import dk.bayes.model.clustergraph.ClusterGraph
import dk.bayes.model.clustergraph.factor._

object TennisDBN {

  //Create player skill variables
  val player1Time0Var = Var(1, 3)
  val player1Time1Var = Var(2, 3)
  val player1Time2Var = Var(3, 3)

  val player2Time0Var = Var(4, 3)
  val player2Time1Var = Var(5, 3)
  val player2Time2Var = Var(6, 3)

  val player3Time1Var = Var(7, 3)
  val player3Time2Var = Var(8, 3)

  //Create match outcome variables
  val match1v2Time0Var = Var(9, 2)
  val match1v2Time1Var = Var(10, 2)
  val match2v3Time1Var = Var(11, 2)
  val match1v2Time2Var = Var(12, 2)
  val match1v3Time2Var = Var(13, 2)
  val match2v3Time2Var = Var(14, 2)

  //Create network parameters
  val priorTypeId = 1
  val emissionTypeId = 2
  val transitionTypeId = 3

  val priorParam = Array(0.2, 0.5, 0.3)

  val emissionParam = Array(
    0.5, 0.5,
    1d / 3, 2d / 3,
    0.25, 0.75,
    2d / 3, 1d / 3,
    0.5, 0.5,
    2d / 5, 3d / 5,
    3d / 4, 1d / 4,
    3d / 5, 2d / 5,
    0.5, 0.5)

  val transitionParam = Array(0.98, 0.01, 0.01, 0.01, 0.98, 0.01, 0.01, 0.02, 0.97)

  def createTennisClusterGraph(): ClusterGraph = {

    //Create factors and cluster graph	
    val tennisGraph = ClusterGraph()

    //Player 1 skills
    tennisGraph.addCluster(player1Time0Var.id, Factor(player1Time0Var, priorParam), Option(priorTypeId))
    tennisGraph.addCluster(player1Time1Var.id, Factor(player1Time0Var, player1Time1Var, transitionParam), Option(transitionTypeId))
    tennisGraph.addCluster(player1Time2Var.id, Factor(player1Time1Var, player1Time2Var, transitionParam), Option(transitionTypeId))

    //Player 2 skills
    tennisGraph.addCluster(player2Time0Var.id, Factor(player2Time0Var, priorParam), Option(priorTypeId))
    tennisGraph.addCluster(player2Time1Var.id, Factor(player2Time0Var, player2Time1Var, transitionParam), Option(transitionTypeId))
    tennisGraph.addCluster(player2Time2Var.id, Factor(player2Time1Var, player2Time2Var, transitionParam))

    //Player 3 skills
    tennisGraph.addCluster(player3Time1Var.id, Factor(player3Time1Var, priorParam), Option(priorTypeId))
    tennisGraph.addCluster(player3Time2Var.id, Factor(player3Time1Var, player3Time2Var, transitionParam), Option(transitionTypeId))

    //Match outcomes
    tennisGraph.addCluster(match1v2Time0Var.id, Factor(player1Time0Var, player2Time0Var, match1v2Time0Var, emissionParam), Option(emissionTypeId))
    tennisGraph.addCluster(match1v2Time1Var.id, Factor(player1Time1Var, player2Time1Var, match1v2Time1Var, emissionParam), Option(emissionTypeId))
    tennisGraph.addCluster(match2v3Time1Var.id, Factor(player2Time1Var, player3Time1Var, match2v3Time1Var, emissionParam), Option(emissionTypeId))
    tennisGraph.addCluster(match1v2Time2Var.id, Factor(player1Time2Var, player2Time2Var, match1v2Time2Var, emissionParam), Option(emissionTypeId))
    tennisGraph.addCluster(match1v3Time2Var.id, Factor(player1Time2Var, player3Time2Var, match1v3Time2Var, emissionParam), Option(emissionTypeId))
    tennisGraph.addCluster(match2v3Time2Var.id, Factor(player2Time2Var, player3Time2Var, match2v3Time2Var, emissionParam), Option(emissionTypeId))

    //Create transition edges
    tennisGraph.addEdges((player1Time0Var.id, player1Time1Var.id), (player1Time1Var.id, player1Time2Var.id))
    tennisGraph.addEdges((player2Time0Var.id, player2Time1Var.id), (player2Time1Var.id, player2Time2Var.id))
    tennisGraph.addEdges((player3Time1Var.id, player3Time2Var.id))

    //Create match outcome edges
    tennisGraph.addEdges((player1Time0Var.id, match1v2Time0Var.id), (player2Time0Var.id, match1v2Time0Var.id))

    tennisGraph.addEdges((player1Time1Var.id, match1v2Time1Var.id), (player2Time1Var.id, match1v2Time1Var.id))
    tennisGraph.addEdges((player2Time1Var.id, match2v3Time1Var.id), (player3Time1Var.id, match2v3Time1Var.id))

    tennisGraph.addEdges((player1Time2Var.id, match1v2Time2Var.id), (player2Time2Var.id, match1v2Time2Var.id))
    tennisGraph.addEdges((player1Time2Var.id, match1v3Time2Var.id), (player3Time2Var.id, match1v3Time2Var.id))
    tennisGraph.addEdges((player2Time2Var.id, match2v3Time2Var.id), (player3Time2Var.id, match2v3Time2Var.id))

    tennisGraph
  }
}