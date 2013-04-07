package dk.bayes.infer.ep

import dk.bayes.infer.ep.util.TennisFactorGraph
import org.junit._
import Assert._
import TennisFactorGraph._
import Assert._

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
  @Ignore @Test def no_result_set {

    ep.calibrate()

    val outcomeMarginal = ep.marginal(outcomeVarId)
    assertEquals(0.24463, outcomeMarginal.getValue((outcomeVarId, 0)), 0.0001)
    assertEquals(0.75537, outcomeMarginal.getValue((outcomeVarId, 1)), 0.0001)
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