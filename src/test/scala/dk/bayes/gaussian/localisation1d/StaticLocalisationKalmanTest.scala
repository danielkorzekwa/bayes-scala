package dk.bayes.gaussian.localisation1d

import org.junit.Assert._
import org.junit.Test

import dk.bayes.gaussian.Gaussian
import dk.bayes.gaussian.KalmanFilter
import dk.bayes.gaussian.LinearGaussian

class StaticLocalisationKalmanTest {

  val priorProb = Gaussian(mu = 3, sigma = 1.5)
  val emissionProb = LinearGaussian(a = 1, b = 0, sigma = 0.9)

  @Test def single_observation {
    val locationPosterior = KalmanFilter.posterior(priorProb, emissionProb.sigma, 0.6)
    
    assertEquals(1.5, locationPosterior.mu, 0.001)
    assertEquals(0.5625, locationPosterior.sigma, 0.001)
  }

  @Test def two_observations {

    val locationPosterior1 = KalmanFilter.posterior(priorProb, emissionProb.sigma, 0.6)
    val locationPosterior2 = KalmanFilter.posterior(locationPosterior1, emissionProb.sigma, 0.62)

    assertEquals(1.161, locationPosterior2.mu, 0.001)
    assertEquals(0.346, locationPosterior2.sigma, 0.001)
  }

  @Test def multiple_100K_observations {

    val lastLocation = (1 to 100).foldLeft(priorProb) { (currLocation, i) =>
      KalmanFilter.posterior(currLocation, emissionProb.sigma, 0.6)
    }

    assertEquals(0.614, lastLocation.mu, 0.001)
    assertEquals(0.008, lastLocation.sigma, 0.001)
  }

}