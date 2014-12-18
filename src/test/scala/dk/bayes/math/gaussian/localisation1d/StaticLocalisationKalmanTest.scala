package dk.bayes.math.gaussian.localisation1d

import org.junit.Assert._
import org.junit.Test

import dk.bayes.math.gaussian.Gaussian
import dk.bayes.math.gaussian.KalmanFilter
import dk.bayes.math.gaussian.LinearGaussian

class StaticLocalisationKalmanTest {

  val priorProb = Gaussian(m = 3, v = 1.5)
  val emissionProb = LinearGaussian(a = 1, b = 0, v = 0.9)

  @Test def single_observation {
    val locationPosterior = KalmanFilter.posterior(priorProb, emissionProb.v, 0.6)
    
    assertEquals(1.5, locationPosterior.m, 0.001)
    assertEquals(0.5625, locationPosterior.v, 0.001)
  }

  @Test def two_observations {

    val locationPosterior1 = KalmanFilter.posterior(priorProb, emissionProb.v, 0.6)
    val locationPosterior2 = KalmanFilter.posterior(locationPosterior1, emissionProb.v, 0.62)

    assertEquals(1.161, locationPosterior2.m, 0.001)
    assertEquals(0.346, locationPosterior2.v, 0.001)
  }

  @Test def multiple_100K_observations {

    val lastLocation = (1 to 100).foldLeft(priorProb) { (currLocation, i) =>
      KalmanFilter.posterior(currLocation, emissionProb.v, 0.6)
    }

    assertEquals(0.614, lastLocation.m, 0.001)
    assertEquals(0.008, lastLocation.v, 0.001)
  }

}