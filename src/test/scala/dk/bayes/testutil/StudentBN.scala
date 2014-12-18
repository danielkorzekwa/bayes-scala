package dk.bayes.testutil

import dk.bayes.model.clustergraph.factor._
import dk.bayes.model.clustergraph.factor.Factor._
import dk.bayes.model.clustergraph.ClusterGraph

/**
 * Bayesian network example, borrowed from 'Daphne Koller, Nir Friedman. Probabilistic Graphical Models, Principles and Techniques, 2009' book.
 *
 * @author Daniel Korzekwa
 */
object StudentBN {

  //Create variables
  val difficultyVar = Var(1, 2)
  val intelliVar = Var(2, 2)
  val gradeVar = Var(3, 3)
  val satVar = Var(4, 2)
  val letterVar = Var(5, 2)

  //Create factors
  val difficultyFactor = Factor(difficultyVar, Array(0.6, 0.4))
  val intelliFactor = Factor(intelliVar, Array(0.7, 0.3))
  val gradeFactor = Factor(intelliVar, difficultyVar, gradeVar, FactorUtil.normalise(Array(0.3, 0.4, 0.3, 0.05, 0.25, 0.7, 0.9, 0.08, 0.02, 0.5, 0.3, 0.2)))
  val satFactor = Factor(intelliVar, satVar, Array(0.95, 0.05, 0.2, 0.8))
  val letterFactor = Factor(gradeVar, letterVar, Array(0.1, 0.9, 0.4, 0.6, 0.99, 0.01))

  def createStudentGraph(): ClusterGraph = {
    val clusterGraph = ClusterGraph()
    clusterGraph.addCluster(difficultyVar.id, difficultyFactor)
    clusterGraph.addCluster(intelliVar.id, intelliFactor)
    clusterGraph.addCluster(gradeVar.id, gradeFactor)
    clusterGraph.addCluster(satVar.id, satFactor)
    clusterGraph.addCluster(letterVar.id, letterFactor)

    clusterGraph.addEdges((1, 3), (2, 3), (2, 4), (3, 5))

    clusterGraph
  }
}