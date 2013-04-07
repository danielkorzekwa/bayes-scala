package dk.bayes.infer.ep.util

import dk.bayes.model.factorgraph.FactorGraph
import dk.bayes.model.factorgraph.GenericFactorGraph
import dk.bayes.model.factor.TableFactor
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

  val skill1Factor = GaussianFactor(skill1VarId, 4, 81)
  val skill2Factor = GaussianFactor(skill2VarId, 41, 25)
  val perf1Factor = LinearGaussianFactor(skill1VarId, perf1VarId, 1, 0, pow(25d / 6, 2))
  val perf2Factor = LinearGaussianFactor(skill2VarId, perf2VarId, 1, 0, pow(25d / 6, 2))
  val perfDiffFactor = DiffGaussianFactor(perf1VarId, perf2VarId, perfDiffVarId)
  val outcomeFactor = TruncGaussianFactor(perfDiffVarId, outcomeVarId, 0)

  def createTennisFactorGraph(): FactorGraph = {
    GenericFactorGraph()
  }
}