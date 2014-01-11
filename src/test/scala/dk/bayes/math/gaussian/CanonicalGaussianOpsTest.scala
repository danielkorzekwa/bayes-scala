package dk.bayes.math.gaussian

import org.junit._
import Assert._
import Linear._

class CanonicalGaussianOpsTest {

  val xId = 1
  val yId = 2

  val x = CanonicalGaussian(Array(xId), Matrix(3), Matrix(1.5))
  val y = CanonicalGaussian(Array(yId), Matrix(5), Matrix(2.5))
  val yGivenx = CanonicalGaussian(Array(xId, yId), Matrix(-0.1), 2, 0.5)

  /**
   * tests for multiply
   */

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

  /**
   * tests for divide
   */

  @Test def divide_multiply {
    val gaussian1 = CanonicalGaussian(Array(xId, yId), Matrix(1.5, 2), Matrix(2, 2, Array(1, 0.7, 0.3, 1.2)))
    val gaussian2 = CanonicalGaussian(Array(xId, yId), Matrix(1.9, 2.6), Matrix(2, 2, Array(1.1, 0.4, 0.6, 1.65)))

    val newGaussian = (gaussian1 / gaussian2) * gaussian2

    assertEquals(Matrix(Array(1.5, 2)).toString(), newGaussian.getMean().toString())
    assertEquals(Matrix(2, 2, Array(1, 0.7, 0.3, 1.2)).toString(), newGaussian.getVariance().toString())
  }

  @Test def multiply_divide {
    val gaussian1 = CanonicalGaussian(Array(xId, yId), Matrix(1.5, 2), Matrix(2, 2, Array(1, 0.7, 0.3, 1.2)))
    val gaussian2 = CanonicalGaussian(Array(xId, yId), Matrix(1.9, 2.6), Matrix(2, 2, Array(1.1, 0.4, 0.6, 1.65)))

    val newGaussian = (gaussian1 * gaussian2) / gaussian2

    assertEquals(Matrix(Array(1.5, 2)).toString(), newGaussian.getMean().toString())
    assertEquals(Matrix(2, 2, Array(1, 0.7, 0.3, 1.2)).toString(), newGaussian.getVariance().toString())
  }

  @Test def divide_mvn {
    val gaussian1 = CanonicalGaussian(Array(xId, yId), Matrix(1.5, 2), Matrix(2, 2, Array(1, 0.7, 0.3, 1.2)))
    val gaussian2 = CanonicalGaussian(Array(xId, yId), Matrix(1.9, 2.6), Matrix(2, 2, Array(1.1, 0.4, 0.6, 1.65)))

    val newGaussian = (gaussian1 * gaussian2)

    assertEquals(Matrix(Array(1.722, 2.245)).toString(), newGaussian.getMean().toString())
    assertEquals(Matrix(2, 2, Array(0.533, 0.3, 0.214, 0.705)).toString(), newGaussian.getVariance().toString())
  }

  @Test def divide_univariate {
    val gaussian1 = CanonicalGaussian(xId, 1.5, 3.4)
    val gaussian2 = CanonicalGaussian(xId, 1.9, 2.1)

    val newGaussian = (gaussian1 / gaussian2)
    val expectedGaussian = gaussian1.toGaussian / gaussian2.toGaussian
    assertEquals(expectedGaussian.m, newGaussian.m, 0.0001)
    assertEquals(expectedGaussian.v, newGaussian.v, 0.0001)
  }
}