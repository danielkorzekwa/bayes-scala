package dk.bayes.infer

import org.junit._
import Assert._
import dk.bayes.model.clustergraph.factor._
import dk.bayes.model.clustergraph.GenericClusterGraph
import dk.bayes.testutil.AssertUtil._

class LoopyBPGettingStarted {

  //Create variables
  val difficultyVar = Var(1, 2)
  val intelliVar = Var(2, 2)
  val gradeVar = Var(3, 3)
  val satVar = Var(4, 2)
  val letterVar = Var(5, 2)

  //Create factors
  val difficultyFactor = Factor(difficultyVar, Array(0.6, 0.4))
  val intelliFactor = Factor(intelliVar, Array(0.7, 0.3))
  val gradeFactor = Factor(intelliVar, difficultyVar, gradeVar, Array(0.3, 0.4, 0.3, 0.05, 0.25, 0.7, 0.9, 0.08, 0.02, 0.5, 0.3, 0.2))
  val satFactor = Factor(intelliVar, satVar, Array(0.95, 0.05, 0.2, 0.8))
  val letterFactor = Factor(gradeVar, letterVar, Array(0.1, 0.9, 0.4, 0.6, 0.99, 0.01))

  //Create cluster graph
  val clusterGraph = GenericClusterGraph()
  clusterGraph.addCluster(1, difficultyFactor)
  clusterGraph.addCluster(2, intelliFactor)
  clusterGraph.addCluster(3, gradeFactor)
  clusterGraph.addCluster(4, satFactor)
  clusterGraph.addCluster(5, letterFactor)

  //Add edges between clusters in a cluster graph
  clusterGraph.addEdges((1, 3), (2, 3), (2, 4), (3, 5))

  //Calibrate cluster graph
  val loopyBP = LoopyBP(clusterGraph)
  loopyBP.calibrate()

  @Test def compute_marginal_for_grade_variable {

    //Get marginal for Grade variable
    val gradeMarginal = loopyBP.marginal(gradeVar.id)
    gradeMarginal.getVariables() // Var(3,3)
    gradeMarginal.getValues() // List(0.3620, 0.2884, 0.3496)

    assertFactor(Factor(Var(3, 3), Array(0.3620, 0.2884, 0.3496)), gradeMarginal, 0.0001)
  }

  @Test def compute_marginal_for_grade_variable_given_SAT_is_high {

    loopyBP.setEvidence(satVar.id, 0)
    loopyBP.calibrate()

    val gradeMarginal = loopyBP.marginal(gradeVar.id)
    gradeMarginal.getVariables() // Var(3,3)
    gradeMarginal.getValues() // List(0.2446, 0.3257, 0.4295)

    assertFactor(Factor(Var(3, 3), Array(0.2446, 0.3257, 0.4295)), gradeMarginal, 0.0001)
  }
}