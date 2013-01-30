package dk.bayes.gaussian.localisation1d

import org.junit.Assert._
import org.junit.Test
import dk.bayes.gaussian.CanonicalGaussian
import dk.bayes.gaussian.Linear.Matrix
import dk.bayes.gaussian.Gaussian
import dk.bayes.gaussian.LinearGaussian

class HMMLocalisationCanonicalGaussianTest {

  val priorProb = Gaussian(mu = 3, sigma = 1.5)
  val transitionProb = LinearGaussian(a = 1, b = 0, sigma = 0.2)
  val emissionProb = LinearGaussian(a = 1, b = 0, sigma = 0.9)

  @Test def single_observation {

    val location1Id = 1
    val location2Id = 2
    val observationId = 3

    val location1 = CanonicalGaussian(location1Id, priorProb.mu, priorProb.sigma)
    val location2 = CanonicalGaussian(Array(location1Id, location2Id), transitionProb.b, transitionProb.sigma, Matrix(transitionProb.a))
    val observation = CanonicalGaussian(Array(location2Id, observationId), emissionProb.b, emissionProb.sigma, Matrix(emissionProb.a))

    val location2Marginal = (location1 * location2).marginalise(location1Id)
    val location2Posterior = (location2Marginal * observation).withEvidence(observationId, 0.6)

    assertEquals(1.430, location2Posterior.getMu.at(0), 0.001)
    assertEquals(0.588, location2Posterior.getSigma.at(0), 0.001)

    assertEquals(3, location2Marginal.getMu.at(0), 0.001)
    assertEquals(1.7, location2Marginal.getSigma.at(0), 0.001)

  }
}