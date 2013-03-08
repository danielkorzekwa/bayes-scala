package dk.bayes.gaussian.localisation1d

import org.junit.Assert._
import org.junit.Test
import dk.bayes.gaussian.CanonicalGaussian
import dk.bayes.gaussian.Linear.Matrix
import dk.bayes.gaussian.Gaussian
import dk.bayes.gaussian.LinearGaussian

class HMMLocalisationCanonicalGaussianTest {

  val priorProb = Gaussian(m = 3, v = 1.5)
  val transitionProb = LinearGaussian(a = 1, b = 0, v = 0.2)
  val emissionProb = LinearGaussian(a = 1, b = 0, v = 0.9)

  @Test def single_observation {

    val location1Id = 1
    val location2Id = 2
    val observationId = 3

    val location1 = priorProb.toCanonical(location1Id)
    val location2 = transitionProb.toCanonical(location1Id, location2Id)
    val observation =emissionProb.toCanonical(location2Id, observationId)

    val location2Marginal = (location1 * location2).marginalise(location1Id)
    val location2Posterior = (location2Marginal * observation).withEvidence(observationId, 0.6)

    assertEquals(1.430, location2Posterior.getMean.at(0), 0.001)
    assertEquals(0.588, location2Posterior.getVariance.at(0), 0.001)

    assertEquals(3, location2Marginal.getMean.at(0), 0.001)
    assertEquals(1.7, location2Marginal.getVariance.at(0), 0.001)

  }
}