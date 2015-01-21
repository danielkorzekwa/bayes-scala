package dk.bayes.math.gaussian.canonical

import org.junit._
import Assert._
import org.ejml.simple.SimpleMatrix
import dk.bayes.math.linear._
import DenseCanonicalGaussianTest._

object DenseCanonicalGaussianTest {

  val x = DenseCanonicalGaussian(3, 1.5)

  val a = Matrix(-0.1)
  val yGivenx = DenseCanonicalGaussian(a, 2, 0.5)
}

class DenseCanonicalGaussianTest {

  /**
   * Tests for class constructor - illegal arguments
   */

  @Test(expected = classOf[IllegalArgumentException]) def constructor_wrong_dimensions_of_k_and_h {
    new DenseCanonicalGaussian(Matrix(2, 2, Array(0d, 0, 0, 0)), Matrix(0), 0)
  }

  @Test(expected = classOf[IllegalArgumentException]) def constructor_k_matrix_is_not_square {
    DenseCanonicalGaussian(Matrix(3, 2, Array(0d, 0, 0, 0, 0, 0)), Matrix(0, 0, 0))
  }

  @Test(expected = classOf[IllegalArgumentException]) def constructor_h_matrix_is_not_column_vector {
    DenseCanonicalGaussian(Matrix(2, 2, Array(0d, 0, 0, 0)), Matrix(1, 3, Array(0d, 0, 0)))
  }

  /**
   * Tests for class constructor - linear gaussian
   */

  @Test def constructor_linear_gaussian {
    val gaussian1 = DenseCanonicalGaussian(a = Matrix(0.7), b = 1.5, v = 2)
    val gaussian2 = DenseCanonicalGaussian(a = Matrix(0.7), b = Matrix(1.5), v = Matrix(2))
    assertGaussian(gaussian1, gaussian2)

    DenseCanonicalGaussian(Matrix(1d, -1d).t, 0, 1e-12)
  }

  @Test def constructor_linear_gaussian_multivariate {

    val gaussian = DenseCanonicalGaussian(a = Matrix(2, 2, Array(1d, 0.2, 0.4, 1)), b = Matrix(0.4, 0.7), v = Matrix(2, 2, Array(1.2, 0.4, 5, 2.3)))
    assertEquals(Matrix(-0.279, 1.441, 0.362, 2.338).toString, gaussian.mean.toString)
    assertEquals(Matrix(4, 4, Array(0.554, 0.138, -0.026, 0.208, 1.662, -0.104, 0.108, -1.229, -0.554, 0.138, -0.009, -0.035, -2.216, -0.277, 0.104, -1.385)).toString, gaussian.variance.toString)
  }

  /**
   * Tests for pdf() method
   */

  @Test def pdf {

    assertEquals(0.398942, DenseCanonicalGaussian(0, 1).pdf(0), 0.0001)
    assertEquals(0.2419, DenseCanonicalGaussian(0, 1).pdf(-1), 0.0001)

    assertEquals(0.10648, DenseCanonicalGaussian(2, 9).pdf(0), 0.0001)
    assertEquals(0.08065, DenseCanonicalGaussian(2, 9).pdf(-1), 0.0001)

    assertEquals(0.03707, DenseCanonicalGaussian(1.65, 0.5).pdf(0), 0.0001)
    assertEquals(0.4607, DenseCanonicalGaussian(1.65, 0.5).pdf(1.2), 0.0001)

    assertEquals(0.0336, DenseCanonicalGaussian(1.7, 0.515).pdf(0), 0.0001)

    assertEquals(0.01111, DenseCanonicalGaussian(Matrix(Array(3, 1.7)), Matrix(2, 2, Array(1.5, -0.15, -0.15, 0.515))).pdf(Matrix(3.5, 0)), 0.0001)
  }

  @Test def pdf_linear_gaussian_cpd {

    assertEquals(0.03707, yGivenx.pdf(Matrix(3.5, 0)), 0.0001)
  }

  /**
   * Tests for marginalise() method
   */

  @Test def marginalise_y {

    val marginalX = (x.extend(2, 0) * yGivenx).marginalise(1)

    assertEquals(Matrix(3).toString(), marginalX.mean.toString())
    assertEquals(Matrix(1.5).toString(), marginalX.variance.toString())
  }

  @Test def marginalise_y_from_gaussian_cpd {

    val marginalX = (yGivenx).marginalise(1)

    assertEquals(Matrix(Double.NaN).toString(), marginalX.mean.toString())
    assertEquals(Matrix(Double.PositiveInfinity).toString(), marginalX.variance.toString())
  }

  @Test def marginalise_x_from_gaussian_cpd {

    val marginalY = (yGivenx).marginalise(0)

    assertEquals(Matrix(Double.NaN).toString(), marginalY.mean.toString())
    assertEquals(Matrix(Double.PositiveInfinity).toString(), marginalY.variance.toString())
  }
  @Test def marginalise_x {

    val marginalY = (x.extend(2, 0) * yGivenx).marginalise(0)

    assertEquals(Matrix(1.7).toString(), marginalY.mean.toString())
    assertEquals(Matrix(0.515).toString(), marginalY.variance.toString())
    assertEquals(0.03360, marginalY.pdf(Matrix(0)), 0.0001)
  }

  @Test def marginalise_y_from_linear_gaussian_times_y_scenario_1 {
    val y = DenseCanonicalGaussian(3, 1.5)

    val yGivenx = DenseCanonicalGaussian(Matrix(1), 0, 0.5)

    val marginalX = (yGivenx * y.extend(2, 1)).marginalise(1)

    assertEquals(3, marginalX.toGaussian.m, 0)
    assertEquals(2, marginalX.toGaussian.v, 0)

  }

  @Test def marginalise_y_from_linear_gaussian_times_y_scenario_2 {
    val y = DenseCanonicalGaussian(3, 1.5)

    val yGivenx = DenseCanonicalGaussian(Matrix(2), 0, 0.5)

    val marginalX = (yGivenx * y.extend(2, 1)).marginalise(1)

    assertEquals(1.5, marginalX.toGaussian.m, 0)
    assertEquals(0.5, marginalX.toGaussian.v, 0)

  }

  @Test def marginalise_y_from_linear_gaussian_times_y_scenario_3 {
    val y = DenseCanonicalGaussian(3, 1.5)

    val yGivenx = DenseCanonicalGaussian(Matrix(1), 0.4, 0.5)

    val marginalX = (yGivenx * y.extend(2, 1)).marginalise(1)

    assertEquals(2.6, marginalX.toGaussian.m, 0.0001)
    assertEquals(2, marginalX.toGaussian.v, 0.0001)

  }

  @Test def marginalise_from_linear_gaussian_multivariate {
    val mean = Matrix(0.1, 0.2, 0.3)
    val variance = Matrix(3, 3, Array(1d, 0.1, 0.2, 0.3, 2, 0.4, 0.5, 0.6, 3))
    val gaussian = DenseCanonicalGaussian(mean, variance)

    val a = Matrix(2, 3, Array(1d, 0, 0, 0, 0, 1))
    val linear = DenseCanonicalGaussian(a, b = Matrix(0d, 0), v = Matrix(2, 2, Array(1d, 0.1, 0.2, 2)))

    val marginal = (gaussian.extend(5, 0) * linear).marginal(3, 4)

    assertEquals(Matrix(0.1, 0.3).toString(), marginal.mean.toString())
    assertEquals(Matrix(2, 2, Array(2, 0.3, 0.7, 5)).toString(), marginal.variance.toString())
  }

  /**
   * Tests for marginal()
   */
  @Test def marginal {
    val y = DenseCanonicalGaussian(3, 1.5)

    val yGivenx = DenseCanonicalGaussian(Matrix(1), 0.4, 0.5)

    val expectedMarginalX = (yGivenx * y.extend(2, 1)).marginalise(1)
    val actualMarginalY = (yGivenx * y.extend(2, 1)).marginal(0)

    assertEquals(expectedMarginalX.toGaussian.m, actualMarginalY.toGaussian.m, 0.0001)
    assertEquals(expectedMarginalX.toGaussian.v, actualMarginalY.toGaussian.v, 0.0001)
  }

  @Test def marginal_two_variables_from_3d_Gaussian {

    val gaussian = DenseCanonicalGaussian(Matrix(1, 2, 3), Matrix(3, 3, Array(1, 0.6, 0.8, 0.4, 2, 0.5, 0.2, 0.25, 3)))

    val marginal = gaussian.marginal(0, 2)

    assertEquals(Matrix(1, 3).toString(), marginal.mean.toString())
    assertEquals(Matrix(2, 2, Array(1, 0.8, 0.2, 3)).toString(), marginal.variance.toString())
  }
  /**
   * Tests for withEvidence() method
   */
  @Test def withEvidence_marginal_y {

    val marginalY = yGivenx.withEvidence(0, 3.5)

    assertEquals(0.03707, marginalY.pdf(Matrix(0)), 0.0001)
    assertEquals(Matrix(1.65).toString(), marginalY.mean.toString())
    assertEquals(Matrix(0.5).toString(), marginalY.variance.toString())
  }

  @Test def withEvidence_marginal_y_given_x {

    val marginalY = (x.extend(2, 0) * yGivenx).withEvidence(0, 3.5) //CanonicalGaussian(Array(xId, yId), Matrix(Array(3, 1.7)), Matrix(2, 2, Array(1.5, -0.15, -0.15, 0.515))).withEvidence(xId, 3.5)

    assertEquals(Matrix(1.65).toString(), marginalY.mean.toString())
    assertEquals(Matrix(0.5).toString(), marginalY.variance.toString())
    assertEquals(0.0111, marginalY.pdf(Matrix(0)), 0.0001d)
  }

  @Test def withEvidence_marginal_x_given_y {

    val marginalX = (x.extend(2, 0) * yGivenx).withEvidence(1, 2.5)

    assertEquals(Matrix(2.7669).toString(), marginalX.mean.toString())
    assertEquals(Matrix(1.4563).toString(), marginalX.variance.toString())
    assertEquals(0.00712, marginalX.pdf(Matrix(0)), 0.0001d)
  }

  @Test def withEvidence_marginal_x_given_y_version2 {

    val marginalX = ((x.extend(2, 0) * yGivenx).withEvidence(1, 2.5))

    assertEquals(Matrix(2.7669).toString(), marginalX.mean.toString())
    assertEquals(Matrix(1.4563).toString(), marginalX.variance.toString())
    assertEquals(0.00712, marginalX.pdf(Matrix(0)), 0.0001d)
  }

  @Test def withEvidence_marginal_x_given_y_version3 {

    val marginalX = (yGivenx * x.extend(2, 0)).withEvidence(1, 2.5)
    assertEquals(Matrix(2.7669).toString(), marginalX.mean.toString())
    assertEquals(Matrix(1.4563).toString(), marginalX.variance.toString())

    assertEquals(0.00712, marginalX.pdf(Matrix(0)), 0.0001d)
  }

  @Test def getMu_getSigma {
    val gaussian = DenseCanonicalGaussian(Matrix(1.65), Matrix(0.5))
    assertEquals(Matrix(1.65).toString(), gaussian.mean.toString())
    assertEquals(Matrix(0.5).toString(), gaussian.variance.toString())

    val (mean, variance) = (gaussian.mean, gaussian.variance)
    assertEquals(Matrix(1.65).toString(), mean.toString())
    assertEquals(Matrix(0.5).toString(), variance.toString())
  }

  private def assertGaussian(expected: DenseCanonicalGaussian, actual: DenseCanonicalGaussian) {
    assertTrue(expected.k.isIdentical(actual.k, 0.0001))
    assertTrue(expected.h.isIdentical(actual.h, 0.0001))
    assertEquals(expected.g, actual.g, 0.0001)
  }

}