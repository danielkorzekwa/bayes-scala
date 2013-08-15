package dk.bayes.math.gaussian.localisation1d

import org.junit._
import Assert._
import dk.bayes.math.gaussian.CanonicalGaussian
import dk.bayes.math.gaussian.Linear._
import dk.bayes.math.gaussian.Gaussian
import dk.bayes.math.gaussian.LinearGaussian

class StaticLocalisationCanonicalGaussianTest {

  val priorProb = Gaussian(m = 3, v = 1.5)
  val emissionProb = LinearGaussian(a = 1, b = 0, v = 0.9)

  @Test def single_observation {

    val locationId = 1
    val observationId = 2

    val location = priorProb.toCanonical(locationId)
    val observation = emissionProb.toCanonical(locationId, observationId)

    val locationPosterior = (location * observation).withEvidence(observationId, 0.6)

    assertEquals(1.5, locationPosterior.getMean.at(0), 0.001)
    assertEquals(0.5625, locationPosterior.getVariance.at(0), 0.001)

  }

  @Test def two_observations {

    val locationId = 1
    val observation1Id = 2
    val observation2Id = 3

    val location = priorProb.toCanonical(locationId)

    val observation1 = emissionProb.toCanonical(locationId, observation1Id)
    val observation2 = emissionProb.toCanonical(locationId, observation2Id)

    val jointProb = location * observation1 * observation2
    val locationPosterior = jointProb.withEvidence(observation1Id, 0.6).withEvidence(observation2Id, 0.62)

    //Alternative approach - applying evidence in a serial order
    //val locationPosterior = (location * observation1).withEvidence(observation1Id, 0.6) * observation2.withEvidence(observation2Id, 0.62)

    assertEquals(1.161, locationPosterior.getMean.at(0), 0.001)
    assertEquals(0.346, locationPosterior.getVariance.at(0), 0.001)

  }

  @Test def multiple_100K_observations {

    val locationId = 1
    val observationId = 2

    val location = priorProb.toCanonical(locationId)
    val observation = emissionProb.toCanonical(locationId, observationId)

    val lastLocation = (1 to 100).foldLeft(location) { (currLocation, i) =>
      (currLocation * observation).withEvidence(observationId, 0.6)
    }

    assertEquals(0.614, lastLocation.getMean.at(0), 0.001)
    assertEquals(0.008, lastLocation.getVariance.at(0), 0.001)
  }
}