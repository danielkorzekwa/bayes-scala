package dk.bayes.math.gaussian.canonical

import org.junit._
import Assert._
import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import dk.bayes.math.linear.isIdentical

class CanonicalLinearGaussianMsgFactoryTest {

  val a = DenseMatrix(0.1, 1, 0.4d).t
  val b = DenseVector(0.4d)
  val v = DenseMatrix(1e-2)
  val linear = DenseCanonicalGaussian(a, b, v)

  val x = DenseCanonicalGaussian(DenseVector(1.0, 1.3, 1.5), DenseMatrix((1.2, 1.1, 1.0), (1.1, 1.2, 1.1), (1.0, 1.1, 1.2)))
  val y = DenseCanonicalGaussian(1, 2)

  @Test def test_msgUp = {

    val marginalX = CanonicalLinearGaussianMsgFactory(a, b, v).msgUp(y)

    assertTrue(isIdentical(DenseMatrix((0.004975, 0.04975, 0.01990), (0.04975, 0.49751, 0.19900), (0.01990, 0.19900, 0.07960)), marginalX.k, 0.0001))
    assertTrue(isIdentical(DenseVector(0.02985074626865636, 0.29850746268656536, 0.11940298507462543), marginalX.h, 0.0001))

  }

  @Test def test_msgDown = {

    val marginalY = CanonicalLinearGaussianMsgFactory(a, b, v).msgDown(x)
    assertEquals(0.3855050115651437, marginalY.k(0, 0), 0.0001)
    assertEquals(0.9252120277563662, marginalY.h(0), 0.0001)

  }
}