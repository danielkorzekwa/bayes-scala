package dk.bayes.math.gaussian.ep

import org.junit._
import Assert._
import dk.bayes.math.gaussian.Gaussian
import dk.bayes.testutil.AssertUtil.assertGaussian
import dk.bayes.math.gaussian.LinearGaussian
import scala.math._
import dk.bayes.math.gaussian.LinearGaussian
import dk.bayes.math.gaussian.LinearGaussian
import dk.bayes.math.gaussian.canonical.CanonicalGaussian
import dk.bayes.math.linear._

/**
 * This is a test for a skill update with TrueSkill rating system in a two-person game, like Tennis.
 *
 * Bayesian inference is performed with Expectation Propagation algorithm.
 *
 */
class TrueSkillTwoPersonGameEPTest {

  /**http://atom.research.microsoft.com/trueskill/rankcalculator.aspx*/
  @Test def skills_marginals_match_true_skill_calculator {
    val skillTransitionFactor = LinearGaussian(1, 0, pow(25d / 300, 2))
    val performanceFactor = LinearGaussian(1, 0, pow(25d / 6, 2))

    val (player1, player2) = computeMarginals(player1Skill = Gaussian(4, 81), player2Skill = Gaussian(41, 25), skillTransitionFactor, performanceFactor)
    assertGaussian(Gaussian(27.1743, 37.5013), player1, 0.0001)
    assertGaussian(Gaussian(33.8460, 20.8610), player2, 0.0001)
  }

  @Test def skills_marginals {
    val skillTransitionFactor = LinearGaussian(1, 0, 0.5)
    val performanceFactor = LinearGaussian(1, 0, 2)

    val (player1a, player2a) = computeMarginals(player1Skill = Gaussian(0, 1), player2Skill = Gaussian(0, 1), skillTransitionFactor, performanceFactor)
    assertGaussian(Gaussian(0.4523, 1.2953), player1a, 0.0001)
    assertGaussian(Gaussian(-0.4523, 1.2953), player2a, 0.0001)

    val (player1b, player2b) = computeMarginals(player1Skill = Gaussian(0, 2), player2Skill = Gaussian(0, 2), skillTransitionFactor, performanceFactor)
    assertGaussian(Gaussian(0.66490, 2.0579), player1b, 0.0001)
    assertGaussian(Gaussian(-0.66490, 2.0579), player2b, 0.0001)

    val (player1c, player2c) = computeMarginals(player1Skill = Gaussian(0, 2), player2Skill = Gaussian(0, 3), skillTransitionFactor, performanceFactor)
    assertGaussian(Gaussian(0.6307, 2.1021), player1c, 0.0001)
    assertGaussian(Gaussian(-0.8830, 2.7201), player2c, 0.0001)

    val (player1d, player2d) = computeMarginals(player1Skill = Gaussian(5, 2), player2Skill = Gaussian(7, 3), skillTransitionFactor, performanceFactor)
    assertGaussian(Gaussian(5.9797, 2.0298), player1d, 0.0001)
    assertGaussian(Gaussian(5.628281, 2.5785), player2d, 0.0001)

  }

  @Test def outcome_marginal {
    val skillTransitionFactor = LinearGaussian(1, 0, pow(25d / 300, 2))
    val performanceFactor = LinearGaussian(1, 0, pow(25d / 6, 2))

    val outcomeMarginal = matchProbability(player1Skill = Gaussian(27.1743, 37.5013), player2Skill = Gaussian(33.8460, 20.8610), skillTransitionFactor, performanceFactor)

    assertEquals(0.24463, outcomeMarginal, 0.0001)
  }

  /**
   * Returns posterior marginals for player1 and player2 given player1 is a winner.
   *
   * Variables:
   * - S1_old Skill for player 1 at the time t-1,  N(m,v)
   * - S2_old Skill for player 2 at the time t-1,  N(m,v)
   * - S1 Skill for player 1 at the time t, N(m,v)
   * - S2 Skill for player 2 at the time t, N(m,v)
   * - P1 Performance for player 1, N(m,v)
   * - P2 Performance for player 2, N(m,v)
   *
   * - D|P1,P2 (Performance difference), N(m,v)
   * - O|D Outcome of the game from a point of view for player 1, {Win,Lose}
   *
   * Factors:
   * f0(S1_old),f1(S1_old,S1),f2(S1,P1),
   * f3(S2_old),f4(S2_old,S2),f5(S2,P2),
   * f6(P1,P2,D),f3(D,O)
   *
   * @return Posterior for [S1,S2]
   */
  private def computeMarginals(player1Skill: Gaussian, player2Skill: Gaussian,
    skillTransitionFactor: LinearGaussian, performanceFactor: LinearGaussian): Tuple2[Gaussian, Gaussian] = {

    //forward messages for player 1
    val m_f0_to_f1 = player1Skill
    val m_f1_to_f2 = (skillTransitionFactor * m_f0_to_f1).marginalise(0).toGaussian
    val m_f2_to_f6 = (performanceFactor * m_f1_to_f2).marginalise(0).toGaussian
    
    //forward messages for player 2
    val m_f3_to_f4 = player2Skill
    val m_f4_to_f5 = (skillTransitionFactor * m_f3_to_f4).marginalise(0).toGaussian
    val m_f5_to_f6 = (performanceFactor * m_f4_to_f5).marginalise(0).toGaussian

    //forward-backward messages for performance difference
    val m_f6_to_f7 = m_f2_to_f6 - m_f5_to_f6
    val m_f7_to_f6 = m_f6_to_f7.truncate(0,true) / m_f6_to_f7

    //backward messages for player 1
    val m_f6_to_f2 = m_f7_to_f6 + m_f5_to_f6
    val m_f2_to_f1 = (performanceFactor.toCanonical() * m_f6_to_f2.toCanonical().extend(2,1)).marginalise(1).toGaussian()

    //backward messages for player 2
    val m_f6_to_f5 = m_f2_to_f6 - m_f7_to_f6
    val m_f5_to_f4 = (performanceFactor.toCanonical() * m_f6_to_f5.toCanonical().extend(2,1)).marginalise(1).toGaussian()

    val s1Marginal = m_f2_to_f1 * m_f1_to_f2
    val s2Marginal = m_f5_to_f4 * m_f4_to_f5

    (s1Marginal, s2Marginal)
  }

  /**
   * Returns the probability of winning a tennis game by player1 against player2
   *
   */
  private def matchProbability(player1Skill: Gaussian, player2Skill: Gaussian,
    skillTransitionFactor: LinearGaussian, performanceFactor: LinearGaussian): Double = {

    //forward messages for player 1
    val m_f0_to_f1 = player1Skill
    val m_f1_to_f2 = (skillTransitionFactor * m_f0_to_f1).marginalise(0)
    val m_f2_to_f6 = (performanceFactor * m_f1_to_f2).marginalise(0)

    //forward messages for player 2
    val m_f3_to_f4 = player2Skill
    val m_f4_to_f5 = (skillTransitionFactor * m_f3_to_f4).marginalise(0)
    val m_f5_to_f6 = (performanceFactor * m_f4_to_f5).marginalise(0)

    val m_f6_to_f7 = m_f2_to_f6 - m_f5_to_f6

    val outcomeMarginal = 1 - m_f6_to_f7.cdf(0)
    outcomeMarginal
  }

}