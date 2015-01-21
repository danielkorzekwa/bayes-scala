package dk.bayes.infer.epnaivebayes

import org.junit._
import Assert._
import dk.bayes.math.gaussian.LinearGaussian
import scala.math._
import dk.bayes.math.gaussian.canonical.CanonicalGaussian
import dk.bayes.math.linear.Matrix
import dk.bayes.dsl.variable.Gaussian

class inferPosteriorTest {

  @Test def test_1d_kalman_single_observation {
    val prior = Gaussian(0.5, 2)

    val likelihoods = List(Gaussian(prior, v = 0.1,yValue=0.7))
    val posterior = inferPosterior(prior, likelihoods)

    assertEquals(0.69047, posterior.m, 0.0001)
    assertEquals(0.0952380, posterior.v, 0.0001)
  }

  @Test def test_1d_kalman_two_observations {
    val prior = Gaussian(3, 1.5)
    val likelihoods = List(Gaussian(prior, v = 0.9, yValue = 0.6), Gaussian(prior, v = 0.9, yValue = 0.62))

    val posterior = inferPosterior(prior, likelihoods)

    assertEquals(1.1615, posterior.m, 0.0001)
    assertEquals(0.3461, posterior.v, 0.0001)
  }

}