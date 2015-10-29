package dk.bayes.dsl.demo.variables

import org.junit._
import org.junit.Assert._

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import dk.bayes.dsl.infer
import dk.bayes.dsl.variable.Gaussian
import dk.bayes.math.linear.isIdentical
import dk.bayes.math.numericops.isIdentical

class DenseClutteredGaussianWithMvnGaussianParentTest {

  @Test def test_not_correlated = {

    val x = Gaussian(DenseVector(0d, 15), new DenseMatrix(2, 2, Array(100.0, 0, 0, 100)))
    val y1 = DenseClutteredGaussianWithMvnGaussianParent(x, xIndex = 1, w = 0.4, a = 10, value = 3)
    val y2 = DenseClutteredGaussianWithMvnGaussianParent(x, xIndex = 1, w = 0.4, a = 10, value = 5)

    val posteriorX = infer(x)

    assertTrue("Posterior mean is incorrect:" + posteriorX.m, isIdentical(posteriorX.m,DenseVector(0, 4.343), 0.001))
    assertTrue("Posterior variance is incorrect:" + posteriorX.v, isIdentical(posteriorX.v,new DenseMatrix(2, 2, Array(100, 0, 0, 4.316)), 0.001))
  }
}