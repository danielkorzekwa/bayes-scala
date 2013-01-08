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

  @Test(expected = classOf[IllegalArgumentException]) def wrong_dimensions_of_k_and_h {
    new CanonicalGaussian(Array(1, 2), Matrix(2, 2, Array(0d, 0, 0, 0)), Matrix(0), 0)
  }

  @Test(expected = classOf[IllegalArgumentException]) def k_matrix_is_not_square {
    CanonicalGaussian(Array(1, 2), Matrix(3, 2, Array(0d, 0, 0, 0, 0, 0)), Matrix(0, 0, 0))
  }

  @Test(expected = classOf[IllegalArgumentException]) def h_matrix_is_not_column_vector {
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
    assertEquals(Matrix(3).toString(), marginalX.getMu().toString())
    assertEquals(Matrix(1.5).toString(), marginalX.getSigma().toString())
  }

  @Test def marginalise_x {
    val x = CanonicalGaussian(Array(xId), Matrix(3), Matrix(1.5))
    val yGivenx = CanonicalGaussian(Array(xId, yId), 2, 0.5, Matrix(-0.1))

    val marginalX = (x * yGivenx).marginalise(xId)

    assertArrayEquals(Array(yId), marginalX.varIds)
    assertEquals(Matrix(1.7).toString(), marginalX.getMu().toString())
    assertEquals(Matrix(0.515).toString(), marginalX.getSigma().toString())
    assertEquals(0.03360, marginalX.pdf(Matrix(0)), 0.0001)
  }

  /**
   * Tests for withEvidence() method
   */
  @Test def givenEvidence {
    val beta = Matrix(-0.1)

    val yGivenX = CanonicalGaussian(Array(xId, yId), 2, 0.5, beta).withEvidence(xId, 3.5)

    assertArrayEquals(Array(yId), yGivenX.varIds)
    assertEquals(0.03707, yGivenX.pdf(Matrix(0)), 0.0001)
    assertEquals(Matrix(1.65).toString(), yGivenX.getMu().toString())
    assertEquals(Matrix(0.5).toString(), yGivenX.getSigma().toString())
  }

  @Test def product_given_x {

    val jointGaussian = CanonicalGaussian(Array(xId, yId), Matrix(Array(3, 1.7)), Matrix(2, 2, Array(1.5, -0.15, -0.15, 0.515))).withEvidence(xId, 3.5)

    assertArrayEquals(Array(yId), jointGaussian.varIds)
    assertEquals(Matrix(1.65).toString(), jointGaussian.getMu().toString())
    assertEquals(Matrix(0.5).toString(), jointGaussian.getSigma().toString())
    assertEquals(0.0111, jointGaussian.pdf(Matrix(0)), 0.0001d)
  }

  @Test def product_given_y {
    val x = CanonicalGaussian(Array(xId), Matrix(3), Matrix(1.5))
    val yGivenx = CanonicalGaussian(Array(xId, yId), 2, 0.5, Matrix(-0.1))

    val jointGaussian = (x * yGivenx).withEvidence(yId, 2.5)

    assertArrayEquals(Array(xId), jointGaussian.varIds)
    assertEquals(Matrix(2.7669).toString(), jointGaussian.getMu().toString())
    assertEquals(Matrix(1.4563).toString(), jointGaussian.getSigma().toString())
    assertEquals(0.00712, jointGaussian.pdf(Matrix(0)), 0.0001d)
  }

  @Test def product_yWithEvidence_x {
    val x = CanonicalGaussian(Array(xId), Matrix(3), Matrix(1.5))
    val yGivenx = CanonicalGaussian(Array(xId, yId), 2, 0.5, Matrix(-0.1)).withEvidence(yId, 2.5)

    val jointGaussian = (x * yGivenx)

    assertArrayEquals(Array(xId), jointGaussian.varIds)
    assertEquals(Matrix(2.7669).toString(), jointGaussian.getMu().toString())
    assertEquals(Matrix(1.4563).toString(), jointGaussian.getSigma().toString())
    assertEquals(0.00712, jointGaussian.pdf(Matrix(0)), 0.0001d)
  }

  @Test def product_yx_given_y {
    val x = CanonicalGaussian(Array(xId), Matrix(3), Matrix(1.5))
    val yGivenx = CanonicalGaussian(Array(xId, yId), 2, 0.5, Matrix(-0.1))

    val jointGaussian = (yGivenx * x).withEvidence(yId, 2.5)
    assertArrayEquals(Array(xId), jointGaussian.varIds)
    assertEquals(Matrix(2.7669).toString(), jointGaussian.getMu().toString())
    assertEquals(Matrix(1.4563).toString(), jointGaussian.getSigma().toString())

    assertEquals(0.00712, jointGaussian.pdf(Matrix(0)), 0.0001d)
  }

  @Test def getMu_getSigma {
    val gaussian = CanonicalGaussian(Array(1), Matrix(1.65), Matrix(0.5))
    assertEquals(Matrix(1.65).toString(), gaussian.getMu().toString())
    assertEquals(Matrix(0.5).toString(), gaussian.getSigma().toString())
  }

}