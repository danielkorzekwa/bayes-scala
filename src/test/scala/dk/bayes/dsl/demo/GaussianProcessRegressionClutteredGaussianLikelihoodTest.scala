package dk.bayes.dsl.demo

import org.junit._
import Assert._
import dk.bayes.dsl.variable.Gaussian
import dk.bayes.math.linear.Matrix
import dk.bayes.infer.gp.cov.CovSEiso
import scala.math._
import dk.bayes.dsl.infer
import dk.bayes.testutil.AssertUtil._
import dk.bayes.dsl.demo.variables.ClutteredGaussian
class GaussianProcessRegressionClutteredGaussianLikelihoodTest {

  @Test def test {

    val fMean = Matrix(0, 0, 0)
    val x = Matrix(1, 2, 3)
    val covFunc = CovSEiso(sf = log(7.5120), ell = log(2.1887))
    val fVar = covFunc.cov(x)
    val f = Gaussian(fMean, fVar) //f variable

    val y0 = ClutteredGaussian(x = f, xIndex = 0, w = 0.4, a = 10, value = 1)
    val y1 = ClutteredGaussian(x = f, xIndex = 1, w = 0.4, a = 10, value = 4)
    val y2 = ClutteredGaussian(x = f, xIndex = 2, w = 0.4, a = 10, value = 9)

    val fPosterior = infer(f)

    assertTrue("fPosterior mean is incorrect: " + fPosterior.m, Matrix(0.972, 4.760, 8.386).isIdentical(fPosterior.m, tol = 0.001))
    assertTrue("fPosterior diag variance is incorrect: " + fPosterior.v.extractDiag, Matrix(5.742, 2.282, 2.456).isIdentical(fPosterior.v.extractDiag, tol = 0.001))
  }
}