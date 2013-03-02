package dk.bayes.gaussian.ep

import org.junit._
import Assert._
import dk.bayes.gaussian.Gaussian

/**
 * This is a test for a skill update with TrueSkill rating system in a two-person game like Tennis.
 *
 * Inference is performed with Expectation Propagation algorithm.
 *
 */
class SingleTennisSkillUpdateEPTest {

  /**
   * Variables (clusters on a cluster graph):
   * - S1 Skill for player1, N(m,v)
   * - S2 Skill for player2, N(m,v)
   * - D|S1,S2 (Skill difference), N(m,v)
   * - O|D Outcome of a game from a view of player 1. {Win,Lose}
   *
   */
  @Test def test {

    val s1 = Gaussian(0, 1)
    val s2 = Gaussian(0, 1)

    //messages passing 
    val m_S1_to_D = s1
    val m_S2_to_D = s2
    val m_D_to_O = m_S1_to_D - m_S2_to_D
    val m_O_to_D = m_D_to_O.truncateUpperTail(0) / m_D_to_O
    val m_D_to_S1 = m_O_to_D + m_S2_to_D

    val s1Marginal = s1 * m_D_to_S1

    assertEquals(0.5641, s1Marginal.m, 0.0001)
    assertEquals(0.6816, s1Marginal.v, 0.0001)
  }

}