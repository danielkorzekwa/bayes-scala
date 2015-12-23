package dk.bayes.math.gaussian.canonical

import org.junit._
import Assert._
import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import dk.bayes.math.linear.isIdentical

class canonicalLinearGaussianMsgUpTest {

  val a = DenseMatrix(0.1, 1, 0.4d).t
  val b = DenseVector(0.4d)
  val v = DenseMatrix(1e-2)
  val linear = DenseCanonicalGaussian(a, b, v)
  val msgUp = DenseCanonicalGaussian(1, 2)

  @Test def test = {

    val marginal = CanonicalLinearGaussianMsgUpFactory(a, b, v).msgUp(msgUp)

    assertTrue(isIdentical(DenseMatrix((0.004975, 0.04975, 0.01990), (0.04975, 0.49751, 0.19900), (0.01990, 0.19900, 0.07960)), marginal.k, 0.0001))
    assertTrue(isIdentical(DenseVector(0.02985074626865636, 0.29850746268656536, 0.11940298507462543), marginal.h, 0.0001))

  }
}