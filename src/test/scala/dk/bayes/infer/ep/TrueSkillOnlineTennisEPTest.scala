package dk.bayes.infer.ep

import dk.bayes.infer.ep.util.TennisFactorGraph
import org.junit._
import Assert._
import TennisFactorGraph._
import Assert._
import com.typesafe.scalalogging.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * This is a test for a skill update with TrueSkill rating system in a two-person game, like Tennis.
 *
 * Bayesian inference is performed with Expectation Propagation algorithm.
 *
 * @author Daniel Korzekwa
 */
class TrueSkillOnlineTennisEPTest {

  val tennisFactorGraph = createTennisFactorGraph()
  val ep = GenericEP(tennisFactorGraph)

  /**http://atom.research.microsoft.com/trueskill/rankcalculator.aspx*/
  @Test def no_result_set {

    for (i <- 1 to 3) {
      ep.calibrate()
      println("---EP calibration completed---")
    }

    val outcomeMarginal = ep.marginal(outcomeVarId)
    assertEquals(0.0009, outcomeMarginal.getValue((outcomeVarId, 0)), 0.00001)
    assertEquals(0.99909, outcomeMarginal.getValue((outcomeVarId, 1)), 0.00001)
  }

  /**http://atom.research.microsoft.com/trueskill/rankcalculator.aspx*/
  @Ignore @Test def player1_wins {

    ep.setEvidence(outcomeVarId, 0)
    ep.calibrate()

    val outcomeMarginal = ep.marginal(outcomeVarId)
    assertEquals(0.24463, outcomeMarginal.getValue((outcomeVarId, 0)), 0.0001)
    assertEquals(0.75537, outcomeMarginal.getValue((outcomeVarId, 1)), 0.0001)
  }

  /**http://atom.research.microsoft.com/trueskill/rankcalculator.aspx*/
  @Ignore @Test def player1_looses {

    ep.setEvidence(outcomeVarId, 1)
    ep.calibrate()

    val outcomeMarginal = ep.marginal(outcomeVarId)
    assertEquals(0.24463, outcomeMarginal.getValue((outcomeVarId, 0)), 0.0001)
    assertEquals(0.75537, outcomeMarginal.getValue((outcomeVarId, 1)), 0.0001)
  }
}