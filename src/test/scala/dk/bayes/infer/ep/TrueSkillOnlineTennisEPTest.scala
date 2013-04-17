package dk.bayes.infer.ep

import dk.bayes.infer.ep.util.TennisFactorGraph
import org.junit._
import Assert._
import TennisFactorGraph._
import Assert._
import com.typesafe.scalalogging.slf4j.Logger
import org.slf4j.LoggerFactory
import dk.bayes.model.factor.GaussianFactor

/**
 * This is a test for a skill update with TrueSkill rating system in a two-person game, like Tennis.
 *
 * Bayesian inference is performed with Expectation Propagation algorithm.
 *
 * @author Daniel Korzekwa
 */
class TrueSkillOnlineTennisEPTest {

  /**http://atom.research.microsoft.com/trueskill/rankcalculator.aspx*/
  @Test def no_result_set {

    val tennisFactorGraph = createTennisFactorGraphAfterPlayer1Won()
    val ep = GenericEP(tennisFactorGraph)

    ep.calibrate()

    val outcomeMarginal = ep.marginal(outcomeVarId)
    assertEquals(0.24463, outcomeMarginal.getValue((outcomeVarId, 0)), 0.00001)
    assertEquals(0.75537, outcomeMarginal.getValue((outcomeVarId, 1)), 0.00001)

    for (i <- 1 to 3) {
      ep.calibrate()
      println("---EP calibration completed---")
    }

    val outcomeMarginal2 = ep.marginal(outcomeVarId)
    assertEquals(0.24463, outcomeMarginal2.getValue((outcomeVarId, 0)), 0.00001)
    assertEquals(0.75537, outcomeMarginal2.getValue((outcomeVarId, 1)), 0.00001)
  }

  /**http://atom.research.microsoft.com/trueskill/rankcalculator.aspx*/
  @Test def player1_wins {
    val tennisFactorGraph = createTennisFactorGraph()
    val ep = GenericEP(tennisFactorGraph)

    ep.setEvidence(outcomeVarId, 0)
    ep.calibrate()
    println("---EP iteration completed---")
    ep.calibrate()
    println("---EP iteration completed---")
    ep.calibrate()
    println("---EP iteration completed---")
    ep.calibrate()
    println("---EP iteration completed---")
    ep.calibrate()

    val outcomeMarginal = ep.marginal(outcomeVarId)
    assertEquals(1, outcomeMarginal.getValue((outcomeVarId, 0)), 0.0001)
    assertEquals(0, outcomeMarginal.getValue((outcomeVarId, 1)), 0.0001)

    val skill1Marginal = ep.marginal(skill1VarId).asInstanceOf[GaussianFactor]
    assertEquals(27.1744, skill1Marginal.m, 0.0001)
    assertEquals(37.4973, skill1Marginal.v, 0.0001)
  }

  /**http://atom.research.microsoft.com/trueskill/rankcalculator.aspx*/
  @Ignore @Test def player1_looses {

    val tennisFactorGraph = createTennisFactorGraph()
    val ep = GenericEP(tennisFactorGraph)

    ep.setEvidence(outcomeVarId, 1)
    ep.calibrate()

    val outcomeMarginal = ep.marginal(outcomeVarId)
    assertEquals(0.24463, outcomeMarginal.getValue((outcomeVarId, 0)), 0.0001)
    assertEquals(0.75537, outcomeMarginal.getValue((outcomeVarId, 1)), 0.0001)
  }
}