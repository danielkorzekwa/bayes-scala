package dk.bayes.infer.ep.util

import dk.bayes.model.factorgraph.FactorGraph
import dk.bayes.model.factorgraph.GenericFactorGraph
import dk.bayes.model.factor.SingleTableFactor
import dk.bayes.model.factor.GaussianFactor
import dk.bayes.model.factor.LinearGaussianFactor
import scala.math._
import dk.bayes.model.factor.DiffGaussianFactor
import dk.bayes.model.factor.TruncGaussianFactor
import java.util.concurrent.atomic.AtomicInteger

/**
 * Factor graph representing 6 tennis games between three tennis players over 3 time slices.
 */
object TennisFactorGraphDBN {

  //Create player skill variables
  val player1Time0VarId = 1
  val player1Time1VarId = 2
  val player1Time2VarId = 3

  val player2Time0VarId = 4
  val player2Time1VarId = 5
  val player2Time2VarId = 6

  val player3Time1VarId = 7
  val player3Time2VarId = 8

  //Create match outcome variables
  val match1v2Time0VarId = 9
  val match1v2Time1VarId = 10
  val match2v3Time1VarId = 11
  val match1v2Time2VarId = 12
  val match1v3Time2VarId = 13
  val match2v3Time2VarId = 14

  def createTennisFactorGraph(): FactorGraph = {
    val varId = new AtomicInteger(15)

    val factorGraph = GenericFactorGraph()

    factorGraph.addFactor(GaussianFactor(player1Time0VarId, 4, 81))
    factorGraph.addFactor(LinearGaussianFactor(player1Time0VarId, player1Time1VarId, 1, 0, pow(25d / 6, 2)))
    factorGraph.addFactor(LinearGaussianFactor(player1Time1VarId, player1Time2VarId, 1, 0, pow(25d / 6, 2)))
    factorGraph.addFactor(GaussianFactor(player2Time0VarId, 4, 81))
    factorGraph.addFactor(LinearGaussianFactor(player2Time0VarId, player2Time1VarId, 1, 0, pow(25d / 6, 2)))
    factorGraph.addFactor(LinearGaussianFactor(player2Time1VarId, player2Time2VarId, 1, 0, pow(25d / 6, 2)))
    factorGraph.addFactor(GaussianFactor(player3Time1VarId, 4, 81))
    factorGraph.addFactor(LinearGaussianFactor(player3Time1VarId, player3Time2VarId, 1, 0, pow(25d / 6, 2)))

    def addTennisGameToFactorGraph(player1VarId: Int, player2VarId: Int, matchVarId: Int) {

      val perf1VarId = varId.getAndIncrement()
      val perf2VarId = varId.getAndIncrement()
      val perfDiffVarId = varId.getAndIncrement()

      factorGraph.addFactor(LinearGaussianFactor(player1VarId, perf1VarId, 1, 0, pow(25d / 6, 2)))
      factorGraph.addFactor(LinearGaussianFactor(player2VarId, perf2VarId, 1, 0, pow(25d / 6, 2)))
      factorGraph.addFactor(DiffGaussianFactor(perf1VarId, perf2VarId, perfDiffVarId))
      factorGraph.addFactor(TruncGaussianFactor(perfDiffVarId, matchVarId, 0))
    }

    addTennisGameToFactorGraph(player1Time0VarId, player2Time0VarId, match1v2Time0VarId)

    addTennisGameToFactorGraph(player1Time1VarId, player2Time1VarId, match1v2Time1VarId)
    addTennisGameToFactorGraph(player2Time1VarId, player3Time1VarId, match2v3Time1VarId)

    addTennisGameToFactorGraph(player1Time2VarId, player2Time2VarId, match1v2Time2VarId)
    addTennisGameToFactorGraph(player1Time2VarId, player3Time2VarId, match1v3Time2VarId)
    addTennisGameToFactorGraph(player2Time2VarId, player3Time2VarId, match2v3Time2VarId)

    factorGraph
  }

}