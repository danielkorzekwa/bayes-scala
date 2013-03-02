package dk.bayes.gaussian.ep

import org.junit.Test

import dk.bayes.gaussian.Gaussian
import dk.bayes.testutil.AssertUtil.assertGaussian

/**
 * This is a test for a skill update with TrueSkill rating system in a two-person game like Tennis.
 *
 * Inference is performed with Expectation Propagation algorithm.
 *
 */
class SingleTennisSkillUpdateEPTest {

  @Test def test_1 {

    assertGaussian(Gaussian(0.5641, 0.6816), s1Marginal(s1 = Gaussian(0, 1), s2 = Gaussian(0, 1)), 0.0001)
    assertGaussian(Gaussian(0.7978, 1.3634), s1Marginal(s1 = Gaussian(0, 2), s2 = Gaussian(0, 2)), 0.0001)
    assertGaussian(Gaussian(0.7136, 1.4907), s1Marginal(s1 = Gaussian(0, 2), s2 = Gaussian(0, 3)), 0.0001)

    assertGaussian(Gaussian(5.5641, 0.6816), s1Marginal(s1 = Gaussian(5, 1), s2 = Gaussian(5, 1)), 0.0001)
    assertGaussian(Gaussian(5.7978, 1.3634), s1Marginal(s1 = Gaussian(5, 2), s2 = Gaussian(5, 2)), 0.0001)
    assertGaussian(Gaussian(5.7136, 1.4907), s1Marginal(s1 = Gaussian(5, 2), s2 = Gaussian(5, 3)), 0.0001)

    assertGaussian(Gaussian(6.3194, 0.5784), s1Marginal(s1 = Gaussian(5, 1), s2 = Gaussian(7, 1)), 0.0001)
    assertGaussian(Gaussian(6.5251, 1.1990), s1Marginal(s1 = Gaussian(5, 2), s2 = Gaussian(7, 2)), 0.0001)
    assertGaussian(Gaussian(6.2890, 1.3695), s1Marginal(s1 = Gaussian(5, 2), s2 = Gaussian(7, 3)), 0.0001)
  }

  /**
   * Returns marginal for player1 skill given player1 is a winner.
   *
   * Variables (clusters on a cluster graph):
   * - S1 Skill for player1, N(m,v)
   * - S2 Skill for player2, N(m,v)
   * - D|S1,S2 (Skill difference), N(m,v)
   * - O|D Outcome of a game from a view of player 1. {Win,Lose}
   */
  private def s1Marginal(s1: Gaussian, s2: Gaussian): Gaussian = {

    //messages passing 
    val m_S1_to_D = s1
    val m_S2_to_D = s2
    val m_D_to_O = m_S1_to_D - m_S2_to_D
    val m_O_to_D = m_D_to_O.truncateUpperTail(0) / m_D_to_O
    val m_D_to_S1 = m_O_to_D + m_S2_to_D

    val s1Marginal = s1 * m_D_to_S1

    s1Marginal
  }

}