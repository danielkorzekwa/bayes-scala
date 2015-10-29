package dk.bayes.math.gaussian.canonical

import org.junit._
import org.junit.Assert._

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import dk.bayes.math.linear._

class DenseCanonicalGaussianOpsTest {

  val x = DenseCanonicalGaussian(DenseVector(3d), DenseMatrix(1.5))
  val y = DenseCanonicalGaussian(DenseVector(5d), DenseMatrix(2.5))
  val yGivenx = DenseCanonicalGaussian(DenseMatrix(-0.1), 2, 0.5)

  /**
   * tests for multiply
   */

  @Test def product_xy ={

    val jointGaussian = x.extend(2, 0) * yGivenx

     assertTrue(isIdentical(DenseVector(Array(3, 1.7)), jointGaussian.mean,0.0001))
     assertTrue(isIdentical(new DenseMatrix(2, 2, Array(1.5, -0.15, -0.15, 0.515)), jointGaussian.variance,0.0001))

    assertEquals(0.0111, jointGaussian.pdf(DenseVector(Array(3.5, 0))), 0.0001d)
    assertEquals(0.1679, jointGaussian.pdf(DenseVector(Array(3d, 2))), 0.0001d)
  }

  @Test def product_yx ={

    val jointGaussian = yGivenx * x.extend(2, 0)

     assertTrue(isIdentical(DenseVector(Array(3, 1.7)), jointGaussian.mean,0.0001))
     assertTrue(isIdentical(new DenseMatrix(2, 2, Array(1.5, -0.15, -0.15, 0.515)), jointGaussian.variance,0.0001))

    assertEquals(0.0111, jointGaussian.pdf(DenseVector(Array(3.5, 0))), 0.0001d)
    assertEquals(0.1679, jointGaussian.pdf(DenseVector(Array(3d, 2))), 0.0001d)
  }

  @Test def product_yGivenx_and_y ={

    val jointGaussian = yGivenx * y.extend(2, 1)

     assertTrue(isIdentical(DenseVector(Array(-30d, 5)), jointGaussian.mean,0.0001))
     assertTrue(isIdentical(new DenseMatrix(2, 2, Array(300, -25, -25, 2.5)), jointGaussian.variance,0.0001))

  }

  /**
   * tests for divide
   */

  @Test def divide_multiply ={
    val gaussian1 = DenseCanonicalGaussian(DenseVector(1.5, 2), new DenseMatrix(2, 2, Array(1, 0.7, 0.7, 1.2)))
    val gaussian2 = DenseCanonicalGaussian(DenseVector(1.9, 2.6),new  DenseMatrix(2, 2, Array(1.1, 0.4, 0.4, 1.65)))

    val newGaussian = (gaussian1 / gaussian2) * gaussian2

     assertTrue(isIdentical(DenseVector(Array(1.5, 2)), newGaussian.mean,0.0001))
     assertTrue("actual=" + newGaussian.variance,isIdentical(new DenseMatrix(2, 2, Array(1, 0.7, 0.7, 1.2)), newGaussian.variance,0.0001))
  }

  @Test def multiply_divide ={
    val gaussian1 = DenseCanonicalGaussian(DenseVector(1.5, 2), new DenseMatrix(2, 2, Array(1, 0.7, 0.7, 1.2)))
    val gaussian2 = DenseCanonicalGaussian(DenseVector(1.9, 2.6), new DenseMatrix(2, 2, Array(1.1, 0.4, 0.4, 1.65)))

    val newGaussian = (gaussian1 * gaussian2) / gaussian2

    assertTrue(isIdentical(DenseVector(1.5, 2), newGaussian.mean,0.0001))
     assertTrue("actual=" + newGaussian.variance,isIdentical(new DenseMatrix(2, 2, Array(1, 0.7, 0.7, 1.2)), newGaussian.variance,0.0001))
  }

  @Test def divide_mvn ={
    val gaussian1 = DenseCanonicalGaussian(DenseVector(1.5, 2), new DenseMatrix(2, 2, Array(1, 0.7, 0.7, 1.2)).t)
    val gaussian2 = DenseCanonicalGaussian(DenseVector(1.9, 2.6), new DenseMatrix(2, 2, Array(1.1, 0.4, 0.4, 1.65)).t)

    val newGaussian = (gaussian1 * gaussian2)

     assertTrue("actual=" + newGaussian.mean,isIdentical(DenseVector(Array(1.7207, 2.2764)), newGaussian.mean,0.001))
     assertTrue("actual=" + newGaussian.variance,isIdentical(new DenseMatrix(2, 2, Array(0.5101, 0.3020, 0.3020, 0.6612)).t, newGaussian.variance,0.001))
  }

  @Test def divide_univariate ={
    val gaussian1 = DenseCanonicalGaussian(1.5, 3.4)
    val gaussian2 = DenseCanonicalGaussian(1.9, 2.1)

    val newGaussian = (gaussian1 / gaussian2)
    val expectedGaussian = gaussian1.toGaussian / gaussian2.toGaussian
    assertEquals(expectedGaussian.m, newGaussian.m, 0.0001)
    assertEquals(expectedGaussian.v, newGaussian.v, 0.0001)
  }
}