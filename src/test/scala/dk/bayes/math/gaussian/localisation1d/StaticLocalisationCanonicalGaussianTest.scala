package dk.bayes.math.gaussian.localisation1d

import org.junit._
import Assert._
import dk.bayes.math.gaussian.canonical.CanonicalGaussian
import dk.bayes.math.linear._
import dk.bayes.math.gaussian.Gaussian
import dk.bayes.math.gaussian.LinearGaussian

class StaticLocalisationCanonicalGaussianTest {

  val priorProb = Gaussian(m = 3, v = 1.5)
  val emissionProb = LinearGaussian(a = 1, b = 0, v = 0.9)

  @Test def single_observation {

    val location = priorProb.toCanonical()
    val observation = emissionProb.toCanonical()

    val locationPosterior = (location.extend(2, 0) * observation).withEvidence(1, 0.6)

    assertEquals(1.5, locationPosterior.mean.at(0), 0.001)
    assertEquals(0.5625, locationPosterior.variance.at(0), 0.001)

  }

  @Test def two_observations {

    val location = priorProb.toCanonical()

    val observation1 = emissionProb.toCanonical()
    val observation2 = emissionProb.toCanonical()

    val jointProb = location * observation1.withEvidence(1, 0.6) * observation2.withEvidence(1, 0.62)
    val locationPosterior = jointProb

    //Alternative approach - applying evidence in a serial order
    //val locationPosterior = (location * observation1).withEvidence(observation1Id, 0.6) * observation2.withEvidence(observation2Id, 0.62)

    assertEquals(1.161, locationPosterior.mean.at(0), 0.001)
    assertEquals(0.346, locationPosterior.variance.at(0), 0.001)

  }

  @Test def multiple_100K_observations {

    val location = priorProb.toCanonical()
    val observation = emissionProb.toCanonical()

    val lastLocation = (1 to 100).foldLeft(location) { (currLocation, i) =>
      (currLocation.extend(2, 0) * observation).withEvidence(1, 0.6)
    }

    assertEquals(0.614, lastLocation.mean.at(0), 0.001)
    assertEquals(0.008, lastLocation.variance.at(0), 0.001)
  }
}