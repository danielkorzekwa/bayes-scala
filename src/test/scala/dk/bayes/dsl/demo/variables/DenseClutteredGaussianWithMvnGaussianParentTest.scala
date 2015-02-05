package dk.bayes.dsl.demo.variables

import org.junit._
import Assert._
import dk.bayes.dsl.variable.Gaussian
import dk.bayes.math.linear.Matrix
import dk.bayes.dsl.infer

class DenseClutteredGaussianWithMvnGaussianParentTest {

  @Test def test_not_correlated {

    val x = Gaussian(Matrix(0, 15), Matrix(2, 2, Array(100.0, 0, 0, 100)))
    val y1 = DenseClutteredGaussianWithMvnGaussianParent(x, xIndex = 1, w = 0.4, a = 10, value = 3)
    val y2 = DenseClutteredGaussianWithMvnGaussianParent(x, xIndex = 1, w = 0.4, a = 10, value = 5)

    val posteriorX = infer(x)

    assertTrue("Posterior mean is incorrect:" + posteriorX.m, posteriorX.m.isIdentical(Matrix(0, 4.343), 0.001))
    assertTrue("Posterior variance is incorrect:" + posteriorX.v, posteriorX.v.isIdentical(Matrix(2, 2, Array(100, 0, 0, 4.316)), 0.001))
  }
}