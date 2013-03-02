package dk.bayes.gaussian

import org.junit._
import Assert._
import org.ejml.simple.SimpleMatrix
import Linear._
import CanonicalGaussianTest._

object CanonicalGaussianTest {

  val xId = 1
  val yId = 2
}

class CanonicalGaussianTest {

  /**
   * Tests for class constructor
   */

  @Test(expected = classOf[IllegalArgumentException]) def constructor_empty_variables {
    new CanonicalGaussian(Array(), Matrix(0), Matrix(1), 0)
  }

  @Test(expected = classOf[IllegalArgumentException]) def constructor_wrong_number_of_variables {
    new CanonicalGaussian(Array(1), Matrix(2, 2, Array(0d, 0, 0, 0)), Matrix(0, 0), 0)
  }

  @Test(expected = classOf[IllegalArgumentException]) def constructor_wrong_dimensions_of_k_and_h {
    new CanonicalGaussian(Array(1, 2), Matrix(2, 2, Array(0d, 0, 0, 0)), Matrix(0), 0)
  }

  @Test(expected = classOf[IllegalArgumentException]) def constructor_k_matrix_is_not_square {
    CanonicalGaussian(Array(1, 2), Matrix(3, 2, Array(0d, 0, 0, 0, 0, 0)), Matrix(0, 0, 0))
  }

  @Test(expected = classOf[IllegalArgumentException]) def constructor_h_matrix_is_not_column_vector {
    CanonicalGaussian(Array(1, 2), Matrix(2, 2, Array(0d, 0, 0, 0)), Matrix(1, 3, Array(0d, 0, 0)))
  }

  /**
   * Tests for pdf() method
   */

  @Test def pdf {

    assertEquals(0.398942, CanonicalGaussian(xId, 0, 1).pdf(0), 0.0001)
    assertEquals(0.2419, CanonicalGaussian(xId, 0, 1).pdf(-1), 0.0001)

    assertEquals(0.10648, CanonicalGaussian(xId, 2, 9).pdf(0), 0.0001)
    assertEquals(0.08065, CanonicalGaussian(xId, 2, 9).pdf(-1), 0.0001)

    assertEquals(0.03707, CanonicalGaussian(xId, 1.65, 0.5).pdf(0), 0.0001)
    assertEquals(0.4607, CanonicalGaussian(xId, 1.65, 0.5).pdf(1.2), 0.0001)

    assertEquals(0.0336, CanonicalGaussian(xId, 1.7, 0.515).pdf(0), 0.0001)

    assertEquals(0.01111, CanonicalGaussian(Array(xId, yId), Matrix(Array(3, 1.7)), Matrix(2, 2, Array(1.5, -0.15, -0.15, 0.515))).pdf(Matrix(3.5, 0)), 0.0001)
  }

  @Test def pdf_linear_gaussian_cpd {

    val betaX = Matrix(-0.1)
    assertEquals(0.03707, CanonicalGaussian(Array(xId, yId), 2, 0.5, betaX).pdf(Matrix(3.5, 0)), 0.0001)
  }

  /**
   * Tests for marginalise() method
   */

  @Test def marginalise_y {
    val x = CanonicalGaussian(Array(xId), Matrix(3), Matrix(1.5))
    val yGivenx = CanonicalGaussian(Array(xId, yId), 2, 0.5, Matrix(-0.1))

    val marginalX = (x * yGivenx).marginalise(yId)

    assertArrayEquals(Array(xId), marginalX.varIds)
    assertEquals(Matrix(3).toString(), marginalX.getMean().toString())
    assertEquals(Matrix(1.5).toString(), marginalX.getVariance().toString())
  }

  @Test def marginalise_y_from_gaussian_cpd {
    val yGivenx = CanonicalGaussian(Array(xId, yId), 2, 0.5, Matrix(-0.1))

    val marginalX = (yGivenx).marginalise(yId)

    assertArrayEquals(Array(xId), marginalX.varIds)
    assertEquals(Matrix(Double.NaN).toString(), marginalX.getMean().toString())
    assertEquals(Matrix(Double.PositiveInfinity).toString(), marginalX.getVariance().toString())
  }

  @Test def marginalise_x {
    val x = CanonicalGaussian(Array(xId), Matrix(3), Matrix(1.5))
    val yGivenx = CanonicalGaussian(Array(xId, yId), 2, 0.5, Matrix(-0.1))

    val marginalY = (x * yGivenx).marginalise(xId)

    assertArrayEquals(Array(yId), marginalY.varIds)
    assertEquals(Matrix(1.7).toString(), marginalY.getMean().toString())
    assertEquals(Matrix(0.515).toString(), marginalY.getVariance().toString())
    assertEquals(0.03360, marginalY.pdf(Matrix(0)), 0.0001)
  }

  /**
   * Tests for withEvidence() method
   */
  @Test def withEvidence_marginal_y {
    val beta = Matrix(-0.1)

    val marginalY = CanonicalGaussian(Array(xId, yId), 2, 0.5, beta).withEvidence(xId, 3.5)

    assertArrayEquals(Array(yId), marginalY.varIds)
    assertEquals(0.03707, marginalY.pdf(Matrix(0)), 0.0001)
    assertEquals(Matrix(1.65).toString(), marginalY.getMean().toString())
    assertEquals(Matrix(0.5).toString(), marginalY.getVariance().toString())
  }

  @Test def withEvidence_marginal_y_given_x {
    val beta = Matrix(-0.1)

    val x = CanonicalGaussian(Array(xId), Matrix(3), Matrix(1.5))
    val yGivenx = CanonicalGaussian(Array(xId, yId), 2, 0.5, beta)

    val marginalY = (x * yGivenx).withEvidence(xId, 3.5) //CanonicalGaussian(Array(xId, yId), Matrix(Array(3, 1.7)), Matrix(2, 2, Array(1.5, -0.15, -0.15, 0.515))).withEvidence(xId, 3.5)

    assertArrayEquals(Array(yId), marginalY.varIds)
    assertEquals(Matrix(1.65).toString(), marginalY.getMean().toString())
    assertEquals(Matrix(0.5).toString(), marginalY.getVariance().toString())
    assertEquals(0.0111, marginalY.pdf(Matrix(0)), 0.0001d)
  }

  @Test def withEvidence_marginal_x_given_y {
    val x = CanonicalGaussian(Array(xId), Matrix(3), Matrix(1.5))
    val yGivenx = CanonicalGaussian(Array(xId, yId), 2, 0.5, Matrix(-0.1))

    val marginalX = (x * yGivenx).withEvidence(yId, 2.5)

    assertArrayEquals(Array(xId), marginalX.varIds)
    assertEquals(Matrix(2.7669).toString(), marginalX.getMean().toString())
    assertEquals(Matrix(1.4563).toString(), marginalX.getVariance().toString())
    assertEquals(0.00712, marginalX.pdf(Matrix(0)), 0.0001d)
  }

  @Test def withEvidence_marginal_x_given_y_version2 {
    val x = CanonicalGaussian(Array(xId), Matrix(3), Matrix(1.5))
    val yGivenx = CanonicalGaussian(Array(xId, yId), 2, 0.5, Matrix(-0.1))

    val marginalX = (x * yGivenx.withEvidence(yId, 2.5))

    assertArrayEquals(Array(xId), marginalX.varIds)
    assertEquals(Matrix(2.7669).toString(), marginalX.getMean().toString())
    assertEquals(Matrix(1.4563).toString(), marginalX.getVariance().toString())
    assertEquals(0.00712, marginalX.pdf(Matrix(0)), 0.0001d)
  }

  @Test def withEvidence_marginal_x_given_y_version3 {
    val x = CanonicalGaussian(Array(xId), Matrix(3), Matrix(1.5))
    val yGivenx = CanonicalGaussian(Array(xId, yId), 2, 0.5, Matrix(-0.1))

    val marginalX = (yGivenx * x).withEvidence(yId, 2.5)
    assertArrayEquals(Array(xId), marginalX.varIds)
    assertEquals(Matrix(2.7669).toString(), marginalX.getMean().toString())
    assertEquals(Matrix(1.4563).toString(), marginalX.getVariance().toString())

    assertEquals(0.00712, marginalX.pdf(Matrix(0)), 0.0001d)
  }

  @Test def getMu_getSigma {
    val gaussian = CanonicalGaussian(Array(1), Matrix(1.65), Matrix(0.5))
    assertEquals(Matrix(1.65).toString(), gaussian.getMean().toString())
    assertEquals(Matrix(0.5).toString(), gaussian.getVariance().toString())
  }

}