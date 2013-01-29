package dk.bayes.gaussian.localisation1d

import org.junit.Assert._
import org.junit.Test

import dk.bayes.gaussian.CanonicalGaussian
import dk.bayes.gaussian.Linear.Matrix

class HMMLocalisationCanonicalGaussianTest {

  val locationMean = 3
  val locationVariance = 1.5

  val transitionNoise = 0.2
  val observationNoise = 0.9
  val observationBias = 0

  @Test def single_observation {

    val location1Id = 1
    val location2Id = 2
    val observationId = 3

    val location1 = CanonicalGaussian(location1Id, locationMean, locationVariance)
    val location2 = CanonicalGaussian(Array(location1Id, location2Id), observationBias, transitionNoise, beta = Matrix(1))
    val observation = CanonicalGaussian(Array(location2Id, observationId), observationBias, observationNoise, beta = Matrix(1))

    val location2Marginal = (location1 * location2).marginalise(location1Id)
    val location2Posterior = (location2Marginal * observation).withEvidence(observationId, 0.6)

    assertEquals(1.430, location2Posterior.getMu.at(0), 0.001)
    assertEquals(0.588, location2Posterior.getSigma.at(0), 0.001)

    assertEquals(3, location2Marginal.getMu.at(0), 0.001)
    assertEquals(1.7, location2Marginal.getSigma.at(0), 0.001)

  }
}