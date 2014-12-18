package dk.bayes.math.gaussian.localisation1d

import org.junit.Assert._
import org.junit.Test
import dk.bayes.math.gaussian.Gaussian
import dk.bayes.math.gaussian.LinearGaussian
import dk.bayes.math.gaussian.KalmanFilter

class HMMLocalisationKalmanTest {

  val priorProb = Gaussian(m = 3, v = 1.5)
  val transitionProb = LinearGaussian(a = 1, b = 0, v = 0.2)
  val emissionProb = LinearGaussian(a = 1, b = 0, v = 0.9)

  @Test def single_observation {

    val location2Marginal = KalmanFilter.marginal(priorProb, transitionProb.v)
    val location2Posterior = KalmanFilter.posterior(location2Marginal, emissionProb.v, 0.6)

    assertEquals(1.430, location2Posterior.m, 0.001)
    assertEquals(0.588, location2Posterior.v, 0.001)

    assertEquals(3, location2Marginal.m, 0.001)
    assertEquals(1.7, location2Marginal.v, 0.001)

  }
}