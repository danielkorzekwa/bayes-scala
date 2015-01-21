package dk.bayes.math.gaussian.canonical

import org.junit._
import org.junit.Assert._
import dk.bayes.math.linear._

class DenseCanonicalGaussianOpsTest {

  val x = DenseCanonicalGaussian(Matrix(3), Matrix(1.5))
  val y = DenseCanonicalGaussian(Matrix(5), Matrix(2.5))
  val yGivenx = DenseCanonicalGaussian(Matrix(-0.1), 2, 0.5)

  /**
   * tests for multiply
   */

  @Test def product_xy {

    val jointGaussian = x.extend(2, 0) * yGivenx

    assertEquals(Matrix(Array(3, 1.7)).toString(), jointGaussian.mean.toString())
    assertEquals(Matrix(2, 2, Array(1.5, -0.15, -0.15, 0.515)).toString(), jointGaussian.variance.toString())

    assertEquals(0.0111, jointGaussian.pdf(Matrix(Array(3.5, 0))), 0.0001d)
    assertEquals(0.1679, jointGaussian.pdf(Matrix(Array(3d, 2))), 0.0001d)
  }

  @Test def product_yx {

    val jointGaussian = yGivenx * x.extend(2, 0)

    assertEquals(Matrix(Array(3, 1.7)).toString(), jointGaussian.mean.toString())
    assertEquals(Matrix(2, 2, Array(1.5, -0.15, -0.15, 0.515)).toString(), jointGaussian.variance.toString())

    assertEquals(0.0111, jointGaussian.pdf(Matrix(Array(3.5, 0))), 0.0001d)
    assertEquals(0.1679, jointGaussian.pdf(Matrix(Array(3d, 2))), 0.0001d)
  }

  @Test def product_yGivenx_and_y {

    val jointGaussian = yGivenx * y.extend(2, 1)

    assertEquals(Matrix(Array(-30d, 5)).toString(), jointGaussian.mean.toString())
    assertEquals(Matrix(2, 2, Array(300, -25, -25, 2.5)).toString(), jointGaussian.variance.toString())

  }

  /**
   * tests for divide
   */

  @Test def divide_multiply {
    val gaussian1 = DenseCanonicalGaussian(Matrix(1.5, 2), Matrix(2, 2, Array(1, 0.7, 0.3, 1.2)))
    val gaussian2 = DenseCanonicalGaussian(Matrix(1.9, 2.6), Matrix(2, 2, Array(1.1, 0.4, 0.6, 1.65)))

    val newGaussian = (gaussian1 / gaussian2) * gaussian2

    assertEquals(Matrix(Array(1.5, 2)).toString(), newGaussian.mean.toString())
    assertEquals(Matrix(2, 2, Array(1, 0.7, 0.3, 1.2)).toString(), newGaussian.variance.toString())
  }

  @Test def multiply_divide {
    val gaussian1 = DenseCanonicalGaussian(Matrix(1.5, 2), Matrix(2, 2, Array(1, 0.7, 0.3, 1.2)))
    val gaussian2 = DenseCanonicalGaussian(Matrix(1.9, 2.6), Matrix(2, 2, Array(1.1, 0.4, 0.6, 1.65)))

    val newGaussian = (gaussian1 * gaussian2) / gaussian2

    assertEquals(Matrix(Array(1.5, 2)).toString(), newGaussian.mean.toString())
    assertEquals(Matrix(2, 2, Array(1, 0.7, 0.3, 1.2)).toString(), newGaussian.variance.toString())
  }

  @Test def divide_mvn {
    val gaussian1 = DenseCanonicalGaussian(Matrix(1.5, 2), Matrix(2, 2, Array(1, 0.7, 0.3, 1.2)))
    val gaussian2 = DenseCanonicalGaussian(Matrix(1.9, 2.6), Matrix(2, 2, Array(1.1, 0.4, 0.6, 1.65)))

    val newGaussian = (gaussian1 * gaussian2)

    assertEquals(Matrix(Array(1.722, 2.245)).toString(), newGaussian.mean.toString())
    assertEquals(Matrix(2, 2, Array(0.533, 0.3, 0.214, 0.705)).toString(), newGaussian.variance.toString())
  }

  @Test def divide_univariate {
    val gaussian1 = DenseCanonicalGaussian(1.5, 3.4)
    val gaussian2 = DenseCanonicalGaussian(1.9, 2.1)

    val newGaussian = (gaussian1 / gaussian2)
    val expectedGaussian = gaussian1.toGaussian / gaussian2.toGaussian
    assertEquals(expectedGaussian.m, newGaussian.m, 0.0001)
    assertEquals(expectedGaussian.v, newGaussian.v, 0.0001)
  }
}