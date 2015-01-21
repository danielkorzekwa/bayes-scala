package dk.bayes.math.gaussian.localisation1d

import org.junit.Assert._
import org.junit.Test
import dk.bayes.math.gaussian.canonical.CanonicalGaussian
import dk.bayes.math.linear._
import dk.bayes.math.gaussian.Gaussian
import dk.bayes.math.gaussian.LinearGaussian

class HMMLocalisationCanonicalGaussianTest {

  val priorProb = Gaussian(m = 3, v = 1.5)
  val transitionProb = LinearGaussian(a = 1, b = 0, v = 0.2)
  val emissionProb = LinearGaussian(a = 1, b = 0, v = 0.9)

  @Test def single_observation {

    val location1 = priorProb.toCanonical()
    val location2 = transitionProb.toCanonical()
    val observation =emissionProb.toCanonical()

    val location2Marginal = (location1.extend(2, 0) * location2).marginalise(0)
    val location2Posterior = (location2Marginal.extend(2, 0) * observation).withEvidence(1, 0.6)

    assertEquals(1.430, location2Posterior.mean.at(0), 0.001)
    assertEquals(0.588, location2Posterior.variance.at(0), 0.001)

    assertEquals(3, location2Marginal.mean.at(0), 0.001)
    assertEquals(1.7, location2Marginal.variance.at(0), 0.001)

  }
}