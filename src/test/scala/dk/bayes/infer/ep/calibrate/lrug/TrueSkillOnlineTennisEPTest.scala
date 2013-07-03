package dk.bayes.infer.ep.calibrate.lrug
import org.junit._
import org.junit.Assert._
import dk.bayes.infer.ep.util.TennisFactorGraph._
import org.junit.Assert._
import dk.bayes.model.factor.GaussianFactor
import dk.bayes.gaussian.Linear._
import dk.bayes.model.factor.BivariateGaussianFactor
import dk.bayes.infer.ep.GenericEP

/**
 * This is a test for a skill update with TrueSkill rating system in a two-person game, like Tennis.
 *
 * Bayesian inference is performed with Expectation Propagation algorithm.
 *
 * @author Daniel Korzekwa
 */
class TrueSkillOnlineTennisEPTest {

  private def progress(currIter: Int) = println("EP iteration: " + currIter)

  /**http://atom.research.microsoft.com/trueskill/rankcalculator.aspx*/
  @Test def variable_marginal_player1_wins {
    val tennisFactorGraph = createTennisFactorGraph()

    val ep = GenericEP(tennisFactorGraph)
    ep.setEvidence(outcomeVarId, true)

    val epCalibrate = LRUGateEPCalibrate(tennisFactorGraph)
    assertEquals(62, epCalibrate.calibrate())

    val outcomeMarginal = ep.marginal(outcomeVarId)
    assertEquals(1, outcomeMarginal.getValue((outcomeVarId, 0)), 0.0001)
    assertEquals(0, outcomeMarginal.getValue((outcomeVarId, 1)), 0.0001)

    val skill1Marginal = ep.marginal(skill1VarId).asInstanceOf[GaussianFactor]
    assertEquals(27.1744, skill1Marginal.m, 0.0001)
    assertEquals(37.4973, skill1Marginal.v, 0.0001)

    val skill2Marginal = ep.marginal(skill2VarId).asInstanceOf[GaussianFactor]
    assertEquals(33.8473, skill2Marginal.m, 0.0001)
    assertEquals(20.8559, skill2Marginal.v, 0.0001)
  }

}