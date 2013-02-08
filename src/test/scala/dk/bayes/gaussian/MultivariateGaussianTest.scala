package dk.bayes.gaussian

import org.junit.Assert.assertEquals
import org.junit.Test

import dk.bayes.gaussian.Linear.Matrix

class MultivariateGaussianTest {

  val xIndex = 0
  val yIndex = 1
  val mu = Matrix(3, 1.7)
  val sigma = Matrix(2, 2, Array(1.5, -0.15, -0.15, 0.515))

  val gaussian = MultivariateGaussian(mu, sigma)

  /**
   * Tests for Gaussian marginalisation
   */
  @Test def marginalise_y {

    val marginalx = gaussian.marginalise(yIndex)

    assertEquals(3, marginalx.toGaussian.mu, 0)
    assertEquals(1.5, marginalx.toGaussian.sigma, 0)
  }

  @Test def marginalise_x {

    val marginalY = gaussian.marginalise(xIndex)

    assertEquals(1.7, marginalY.toGaussian.mu, 0)
    assertEquals(0.515, marginalY.toGaussian.sigma, 0)
    assertEquals(0.03360, marginalY.toGaussian.pdf(0), 0.0001)
  }

  /**
   * Tests for withEvidence() method
   */

  @Test def withEvidence_marginal_y_given_x {

    val marginalY = gaussian.withEvidence(xIndex, 3.5)

    assertEquals(1.65, marginalY.toGaussian.mu, 0)
    assertEquals(0.5, marginalY.toGaussian.sigma, 0)
    assertEquals(0.03707, marginalY.toGaussian.pdf(0), 0.0001)
  }

  @Test def withEvidence_marginal_x_given_y {

    val marginalX = gaussian.withEvidence(yIndex, 2.5)

    assertEquals(2.7669, marginalX.toGaussian.mu, 0.0001)
    assertEquals(1.4563, marginalX.toGaussian.sigma, 0.0001)
    assertEquals(0.0238, marginalX.toGaussian.pdf(0), 0.0001d)
  }

  /**
   * Tests for pdf() method
   */

  @Test def pdf {

    assertEquals(0.398942, MultivariateGaussian(0, 1).pdf(0), 0.0001)
    assertEquals(0.2419, MultivariateGaussian(0, 1).pdf(-1), 0.0001)

    assertEquals(0.10648, MultivariateGaussian(2, 9).pdf(0), 0.0001)
    assertEquals(0.08065, MultivariateGaussian(2, 9).pdf(-1), 0.0001)

    assertEquals(0.03707, MultivariateGaussian(1.65, 0.5).pdf(0), 0.0001)
    assertEquals(0.4607, MultivariateGaussian(1.65, 0.5).pdf(1.2), 0.0001)

    assertEquals(0.0336, MultivariateGaussian(1.7, 0.515).pdf(0), 0.0001)

    assertEquals(0.01111, MultivariateGaussian(Matrix(Array(3, 1.7)), Matrix(2, 2, Array(1.5, -0.15, -0.15, 0.515))).pdf(Matrix(3.5, 0)), 0.0001)

  }
}