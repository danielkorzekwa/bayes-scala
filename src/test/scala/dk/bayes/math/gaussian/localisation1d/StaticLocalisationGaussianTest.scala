package dk.bayes.math.gaussian.localisation1d

import org.junit._
import Assert._
import dk.bayes.math.gaussian._

class StaticLocalisationGaussianTest {

  val priorProb = Gaussian(m = 3, v = 1.5)
  val emissionProb = LinearGaussian(a = 1, b = 0, v = 0.9)

  @Test def single_observation {

    val locationPosterior = (priorProb * emissionProb).withEvidence(1, 0.6)

    assertEquals(1.5, locationPosterior.m.at(0), 0.001)
    assertEquals(0.5625, locationPosterior.v.at(0), 0.001)
  }

  @Test def two_observations {

    val locationPosterior1 = (priorProb * emissionProb).withEvidence(1, 0.6)
    val locationPosterior2 = (locationPosterior1 * emissionProb).withEvidence(1, 0.62)

    assertEquals(1.161, locationPosterior2.m.at(0), 0.001)
    assertEquals(0.346, locationPosterior2.v.at(0), 0.001)
  }

  @Test def multiple_100K_observations {

    val lastLocation = (1 to 100).foldLeft(priorProb) { (currLocation, i) =>
      (currLocation * emissionProb).withEvidence(1, 0.6).toGaussian
    }

    assertEquals(0.614, lastLocation.m, 0.001)
    assertEquals(0.008, lastLocation.v, 0.001)
  }

}