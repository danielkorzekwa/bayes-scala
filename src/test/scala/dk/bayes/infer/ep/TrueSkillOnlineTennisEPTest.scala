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

  private def progress(currIter: Int) = println("EP iteration: " + currIter)

  /**http://atom.research.microsoft.com/trueskill/rankcalculator.aspx*/
  @Test def no_result_set {

    val tennisFactorGraph = createTennisFactorGraphAfterPlayer1Won()
    val ep = GenericEP(tennisFactorGraph)

    assertEquals(2, ep.calibrate(10, progress))

    val outcomeMarginal = ep.marginal(outcomeVarId)
    assertEquals(0.24463, outcomeMarginal.getValue((outcomeVarId, 0)), 0.00001)
    assertEquals(0.75537, outcomeMarginal.getValue((outcomeVarId, 1)), 0.00001)

    val skill1Marginal = ep.marginal(skill1VarId).asInstanceOf[GaussianFactor]
    assertEquals(27.1742, skill1Marginal.m, 0.0001)
    assertEquals(37.5013, skill1Marginal.v, 0.0001)

    val skill2Marginal = ep.marginal(skill2VarId).asInstanceOf[GaussianFactor]
    assertEquals(33.846, skill2Marginal.m, 0.0001)
    assertEquals(20.861, skill2Marginal.v, 0.0001)

    assertEquals(1, ep.calibrate(10, progress))

    val outcomeMarginal2 = ep.marginal(outcomeVarId)
    assertEquals(0.24463, outcomeMarginal2.getValue((outcomeVarId, 0)), 0.00001)
    assertEquals(0.75537, outcomeMarginal2.getValue((outcomeVarId, 1)), 0.00001)

    val skill1MarginalLater = ep.marginal(skill1VarId).asInstanceOf[GaussianFactor]
    assertEquals(27.1742, skill1MarginalLater.m, 0.0001)
    assertEquals(37.5013, skill1MarginalLater.v, 0.0001)

    val skill2MarginalLater = ep.marginal(skill2VarId).asInstanceOf[GaussianFactor]
    assertEquals(33.846, skill2MarginalLater.m, 0.0001)
    assertEquals(20.861, skill2MarginalLater.v, 0.0001)
  }

  /**http://atom.research.microsoft.com/trueskill/rankcalculator.aspx*/
  @Test def player1_wins {
    val tennisFactorGraph = createTennisFactorGraph()
    val ep = GenericEP(tennisFactorGraph)

    ep.setEvidence(outcomeVarId, 0)
    assertEquals(7, ep.calibrate(70, progress))

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

  /**http://atom.research.microsoft.com/trueskill/rankcalculator.aspx*/
  @Test def player1_looses {

    val tennisFactorGraph = createTennisFactorGraphAfterPlayer1Won()
    val ep = GenericEP(tennisFactorGraph)

    ep.setEvidence(outcomeVarId, 1)
    assertEquals(7, ep.calibrate(100, progress))

    val outcomeMarginal = ep.marginal(outcomeVarId)
    assertEquals(0, outcomeMarginal.getValue((outcomeVarId, 0)), 0.0001)
    assertEquals(1, outcomeMarginal.getValue((outcomeVarId, 1)), 0.0001)

    val skill1Marginal = ep.marginal(skill1VarId).asInstanceOf[GaussianFactor]
    assertEquals(25.558, skill1Marginal.m, 0.0001)
    assertEquals(30.5446, skill1Marginal.v, 0.0001)

    val skill2Marginal = ep.marginal(skill2VarId).asInstanceOf[GaussianFactor]
    assertEquals(34.745, skill2Marginal.m, 0.0001)
    assertEquals(18.7083, skill2Marginal.v, 0.0001)
  }
}