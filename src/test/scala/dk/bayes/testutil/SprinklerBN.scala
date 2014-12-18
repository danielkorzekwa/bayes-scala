package dk.bayes.testutil

import dk.bayes.model.clustergraph.factor._
import dk.bayes.model.clustergraph.factor.Factor._
import dk.bayes.model.clustergraph.ClusterGraph

/**
 * Bayesian network example, borrowed from 'Adnan Darwiche. Modeling and Reasoning with Bayesian Networks, 2009' book.
 *
 * @author Daniel Korzekwa
 */
object SprinklerBN {

  //Create variables
  val winterVar = Var(1, 2)
  val sprinklerVar = Var(2, 2)
  val rainVar = Var(3, 2)
  val wetGrassVar = Var(4, 2)
  val slipperyRoadVar = Var(5, 2)

  //Create factors
  val winterFactor = Factor(winterVar, Array(0.6, 0.4))
  val sprinklerFactor = Factor(winterVar, sprinklerVar, Array(0.2, 0.8, 0.75, 0.25))
  val rainFactor = Factor(winterVar, rainVar, Array(0.8, 0.2, 0.1, 0.9))
  val wetGrassFactor = Factor(sprinklerVar, rainVar, wetGrassVar, Array(0.95, 0.05, 0.9, 0.1, 0.8, 0.2, 0.01, 0.99))
  val slipperyRoadFactor = Factor(rainVar, slipperyRoadVar, Array(0.7, 0.3, 0.01, 0.99))

  def createSprinklerGraph(): ClusterGraph = {
    val clusterGraph = ClusterGraph()

    clusterGraph.addCluster(winterVar.id, winterFactor)
    clusterGraph.addCluster(sprinklerVar.id, sprinklerFactor)
    clusterGraph.addCluster(rainVar.id, rainFactor)
    clusterGraph.addCluster(wetGrassVar.id, wetGrassFactor)
    clusterGraph.addCluster(slipperyRoadVar.id, slipperyRoadFactor)

    clusterGraph.addEdges((1, 2), (1, 3), (2, 4), (3, 4), (3, 5))

    clusterGraph
  }
} 