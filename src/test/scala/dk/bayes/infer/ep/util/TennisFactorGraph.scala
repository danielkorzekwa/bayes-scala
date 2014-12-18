package dk.bayes.infer.ep.util

import dk.bayes.model.factorgraph.FactorGraph
import dk.bayes.model.factorgraph.GenericFactorGraph
import dk.bayes.model.factor.SingleTableFactor
import dk.bayes.model.factor.GaussianFactor
import dk.bayes.model.factor.LinearGaussianFactor
import scala.math._
import dk.bayes.model.factor.DiffGaussianFactor
import dk.bayes.model.factor.TruncGaussianFactor

/**
 * Factor graph representing a single game between two tennis players.
 */
object TennisFactorGraph {

  val skill1VarId = 1
  val skill2VarId = 2
  val perf1VarId = 3
  val perf2VarId = 4
  val perfDiffVarId = 5
  val outcomeVarId = 6

  private val skill1Factor = GaussianFactor(skill1VarId, 4, 81)
  private val skill2Factor = GaussianFactor(skill2VarId, 41, 25)
  private val perf1Factor = LinearGaussianFactor(skill1VarId, perf1VarId, 1, 0, pow(25d / 6, 2))
  private val perf2Factor = LinearGaussianFactor(skill2VarId, perf2VarId, 1, 0, pow(25d / 6, 2))
  private val perfDiffFactor = DiffGaussianFactor(perf1VarId, perf2VarId, perfDiffVarId)
  private val outcomeFactor = TruncGaussianFactor(perfDiffVarId, outcomeVarId, 0)

  def createTennisFactorGraph(): FactorGraph = {
    val factorGraph = GenericFactorGraph()

    factorGraph.addFactor(skill1Factor)
    factorGraph.addFactor(skill2Factor)
    factorGraph.addFactor(perf1Factor)
    factorGraph.addFactor(perf2Factor)
    factorGraph.addFactor(perfDiffFactor)
    factorGraph.addFactor(outcomeFactor)

    factorGraph
  }
  
    def createTennisFactorGraphWithGenFactor(): FactorGraph = {
    val factorGraph = GenericFactorGraph()

    factorGraph.addFactor(skill1Factor)
    factorGraph.addFactor(skill2Factor)
    factorGraph.addFactor(perf1Factor)
    factorGraph.addFactor(perf2Factor)
    factorGraph.addFactor(GenericDiffGaussianFactor(List(perf1VarId, perf2VarId, perfDiffVarId)))
    factorGraph.addFactor(outcomeFactor)

    factorGraph
  }
  
  
   def createTennisFactorGraphAfterPlayer1Won(): FactorGraph = {
    val factorGraph = GenericFactorGraph()

    factorGraph.addFactor(GaussianFactor(skill1VarId, 27.1743, 37.5013))
    factorGraph.addFactor(GaussianFactor(skill2VarId, 33.8460, 20.8610))
    factorGraph.addFactor(perf1Factor)
    factorGraph.addFactor(perf2Factor)
    factorGraph.addFactor(perfDiffFactor)
    factorGraph.addFactor(outcomeFactor)

    factorGraph
  }
}