package dk.bayes.dsl.demo

import scala.math._

import org.junit._
import org.junit.Assert._

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import breeze.linalg.diag
import dk.bayes.dsl.demo.variables.ClutteredGaussian
import dk.bayes.dsl.infer
import dk.bayes.dsl.variable.Gaussian
import dk.bayes.infer.gp.cov.CovSEiso
import dk.bayes.math.linear.isIdentical
import dk.bayes.testutil.AssertUtil._
class GaussianProcessRegressionClutteredGaussianLikelihoodTest {

  @Test def test:Unit = {

    val fMean = DenseVector(0d, 0, 0)
    val x = DenseMatrix(1d, 2, 3)
    val covFunc = CovSEiso(sf = log(7.5120), ell = log(2.1887))
    val fVar = covFunc.cov(x)
    val f = Gaussian(fMean, fVar) //f variable

    val y0 = ClutteredGaussian(x = f, xIndex = 0, w = 0.4, a = 10, value = 1)
    val y1 = ClutteredGaussian(x = f, xIndex = 1, w = 0.4, a = 10, value = 4)
    val y2 = ClutteredGaussian(x = f, xIndex = 2, w = 0.4, a = 10, value = 9)

    val fPosterior = infer(f)

    assertTrue("fPosterior mean is incorrect: " + fPosterior.m, isIdentical(DenseVector(0.972, 4.760, 8.386), fPosterior.m, tol = 0.001))
    assertTrue("fPosterior diag variance is incorrect: " + diag(fPosterior.v), isIdentical(DenseVector(5.742, 2.282, 2.456), diag(fPosterior.v), tol = 0.001))
  }
}