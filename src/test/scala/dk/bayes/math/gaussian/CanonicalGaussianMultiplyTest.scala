package dk.bayes.math.gaussian

import org.junit._
import Assert._
import Linear._

class CanonicalGaussianMultiplyTest {

  val xId = 1
  val yId = 2

  val x = CanonicalGaussian(Array(xId), Matrix(3), Matrix(1.5))
  val y = CanonicalGaussian(Array(yId), Matrix(5), Matrix(2.5))
  val yGivenx = CanonicalGaussian(Array(xId, yId), Matrix(-0.1), 2, 0.5)

  @Test def product_xy {

    val jointGaussian = x * yGivenx

    assertArrayEquals(Array(xId, yId), jointGaussian.varIds)
    assertEquals(Matrix(Array(3, 1.7)).toString(), jointGaussian.getMean().toString())
    assertEquals(Matrix(2, 2, Array(1.5, -0.15, -0.15, 0.515)).toString(), jointGaussian.getVariance().toString())

    assertEquals(0.0111, jointGaussian.pdf(Matrix(Array(3.5, 0))), 0.0001d)
    assertEquals(0.1679, jointGaussian.pdf(Matrix(Array(3d, 2))), 0.0001d)
  }

  @Test def product_yx {

    val jointGaussian = yGivenx * x

    assertArrayEquals(Array(xId, yId), jointGaussian.varIds)
    assertEquals(Matrix(Array(3, 1.7)).toString(), jointGaussian.getMean().toString())
    assertEquals(Matrix(2, 2, Array(1.5, -0.15, -0.15, 0.515)).toString(), jointGaussian.getVariance().toString())

    assertEquals(0.0111, jointGaussian.pdf(Matrix(Array(3.5, 0))), 0.0001d)
    assertEquals(0.1679, jointGaussian.pdf(Matrix(Array(3d, 2))), 0.0001d)
  }

  @Test def product_yGivenx_and_y {

    val jointGaussian = yGivenx * y

    assertArrayEquals(Array(xId, yId), jointGaussian.varIds)
    assertEquals(Matrix(Array(-30d, 5)).toString(), jointGaussian.getMean().toString())
    assertEquals(Matrix(2, 2, Array(300, -25, -25, 2.5)).toString(), jointGaussian.getVariance().toString())

  }
}