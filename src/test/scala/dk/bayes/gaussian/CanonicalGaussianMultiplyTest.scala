package dk.bayes.gaussian

import org.junit._
import Assert._

import Linear._

class CanonicalGaussianMultiplyTest {

  val xId = 1
  val yId = 2

  @Test def product_xy {
    val x = CanonicalGaussian(Array(xId), Matrix(3), Matrix(1.5))
    val yGivenx = CanonicalGaussian(Array(xId, yId), 2, 0.5, Matrix(-0.1))

    val jointGaussian = x * yGivenx
    assertEquals(Matrix(Array(3, 1.7)).toString(), jointGaussian.getMu().toString())
    assertEquals(Matrix(2, 2, Array(1.5, -0.15, -0.15, 0.515)).toString(), jointGaussian.getSigma().toString())

    assertEquals(0.0111, jointGaussian.pdf(Matrix(Array(3.5, 0))), 0.0001d)
    assertEquals(0.1679, jointGaussian.pdf(Matrix(Array(3d, 2))), 0.0001d)
  }

  @Test def product_yx {
    val x = CanonicalGaussian(Array(xId), Matrix(3), Matrix(1.5))
    val yGivenx = CanonicalGaussian(Array(xId, yId), 2, 0.5, Matrix(-0.1))

    val jointGaussian = yGivenx * x
    assertEquals(Matrix(Array(3, 1.7)).toString(), jointGaussian.getMu().toString())
    assertEquals(Matrix(2, 2, Array(1.5, -0.15, -0.15, 0.515)).toString(), jointGaussian.getSigma().toString())

    assertEquals(0.0111, jointGaussian.pdf(Matrix(Array(3.5, 0))), 0.0001d)
    assertEquals(0.1679, jointGaussian.pdf(Matrix(Array(3d, 2))), 0.0001d)
  }

}