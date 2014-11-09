package dk.bayes.math.gaussian

import org.junit.Assert.assertEquals
import org.junit.Test
import dk.bayes.math.linear._

class MultivariateGaussianTest {

  val xIndex = 0
  val yIndex = 1
  val mean = Matrix(3, 1.7)
  val variance = Matrix(2, 2, Array(1.5, -0.15, -0.15, 0.515))

  val gaussian = MultivariateGaussian(mean, variance)

  /**
   * Tests for draw() sample
   */
  @Test def draw {

    val mean = Matrix(0, 0)
    val variance = Matrix(2, 2, Array(1d, 0.99, 0.99, 1))

    val gaussian = MultivariateGaussian(mean, variance)
    println("Sampling from mvn gaussian:" + gaussian.draw(randSeed=98785454).toList)
  }

  /**
   * Tests for Gaussian marginalisation
   */
  @Test def marginalise_y {

    val marginalx = gaussian.marginalise(yIndex)

    assertEquals(3, marginalx.toGaussian.m, 0)
    assertEquals(1.5, marginalx.toGaussian.v, 0)
  }

  @Test def marginalise_x {

    val marginalY = gaussian.marginalise(xIndex)

    assertEquals(1.7, marginalY.toGaussian.m, 0)
    assertEquals(0.515, marginalY.toGaussian.v, 0)
    assertEquals(0.03360, marginalY.toGaussian.pdf(0), 0.0001)
  }

  /**
   * Tests for withEvidence() method
   */

  @Test def withEvidence_marginal_y_given_x {

    val marginalY = gaussian.withEvidence(xIndex, 3.5)

    assertEquals(1.65, marginalY.toGaussian.m, 0)
    assertEquals(0.5, marginalY.toGaussian.v, 0)
    assertEquals(0.03707, marginalY.toGaussian.pdf(0), 0.0001)
  }

  @Test def withEvidence_marginal_x_given_y {

    val marginalX = gaussian.withEvidence(yIndex, 2.5)

    assertEquals(2.7669, marginalX.toGaussian.m, 0.0001)
    assertEquals(1.4563, marginalX.toGaussian.v, 0.0001)
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