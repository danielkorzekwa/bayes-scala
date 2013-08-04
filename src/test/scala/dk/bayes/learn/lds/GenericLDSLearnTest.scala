package dk.bayes.learn.lds

import org.junit._
import Assert._
import dk.bayes.model.factor.BivariateGaussianFactor
import dk.bayes.gaussian.Linear._

class GenericLDSLearnTest {

  /**
   * Tests for M-step (learning A and Q) - single sequence of latent variables.
   */
  @Test def mstep_for_a_and_q_single_stat {
    val data = Vector(TransitionStat(t0Mean = 1.99966, t0Var = 0.49020, t1Mean = 3.99959, t1Var = 10.95929, cov = 0.9808509))

    assertEquals(2.0002, GenericLDSLearn.newA(data), 0.0001)
    assertEquals(8.9966, GenericLDSLearn.newQ(data), 0.0001)
  }

  @Test def mstep_for_a_and_q_two_stats {
    val data = Vector(
      TransitionStat(t0Mean = 1.99966, t0Var = 0.49020, t1Mean = 3.99959, t1Var = 10.95929, cov = 0.9808509),
      TransitionStat(t0Mean = 3.99959, t0Var = 10.95929, t1Mean = 8, t1Var = 52.78515, cov = 21.89761))

    assertEquals(1.9994, GenericLDSLearn.newA(data), 0.0001)
    assertEquals(9.0142, GenericLDSLearn.newQ(data), 0.0001)
  }

  @Test def mstep_for_a_and_q_four_stats {
    val data = Vector(
      TransitionStat(t0Mean = 1.99966, t0Var = 0.49020, t1Mean = 3.99959, t1Var = 10.95929, cov = 0.9808509),
      TransitionStat(t0Mean = 3.99959, t0Var = 10.95929, t1Mean = 8, t1Var = 52.78515, cov = 21.89761),
      TransitionStat(t0Mean = 2.99966, t0Var = 0.49020, t1Mean = 6.99959, t1Var = 10.95929, cov = 0.9808509),
      TransitionStat(t0Mean = 6.99959, t0Var = 10.95929, t1Mean = 12, t1Var = 52.78515, cov = 21.89761))

    assertEquals(1.8906, GenericLDSLearn.newA(data), 0.0001)
    assertEquals(9.9621, GenericLDSLearn.newQ(data), 0.0001)
  }

  /**
   * Tests for pi and V (prior parameters: mean and variance)
   */

  @Test(expected = classOf[IllegalArgumentException]) def learn_prior_mean_no_variables {
    val data = Vector()
    GenericLDSLearn.newPi(data)
  }

  @Test(expected = classOf[IllegalArgumentException]) def learn_prior_variance_no_variables {
    val data = Vector()
    GenericLDSLearn.newV(data)
  }

  @Test def learn_prior_parameters_single_variable {
    val data = Vector(PriorStat(m = 2, v = 0.5))

    val pi = GenericLDSLearn.newPi(data)
    val V = GenericLDSLearn.newV(data)

    assertEquals(2, pi, 0.0001)
    assertEquals(0.5, V, 0.0001)
  }

  @Test def learn_prior_parameters_multiple_variables {
    val data = Vector(
      PriorStat(m = 2, v = 0.5),
      PriorStat(m = 3, v = 0.1),
      PriorStat(m = 8, v = 0.35))

    val pi = GenericLDSLearn.newPi(data)
    val V = GenericLDSLearn.newV(data)

    assertEquals(4.3333, pi, 0.0001)
    assertEquals(7.2056, V, 0.0001)
  }

  @Test def learn_prior_parameters_two_the_same_variables {
    val data = Vector(
      PriorStat(m = 3, v = 0.5),
      PriorStat(m = 3, v = 0.5))

    val pi = GenericLDSLearn.newPi(data)
    val V = GenericLDSLearn.newV(data)

    assertEquals(3, pi, 0.0001)
    assertEquals(0.5, V, 0.0001)
  }

}