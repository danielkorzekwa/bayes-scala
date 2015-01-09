package dk.bayes.dsl.demo

import org.junit._
import Assert._
import dk.bayes.dsl.variable.Gaussian
import dk.bayes.math.linear.Matrix
import dk.bayes.infer.gp.cov.CovSEiso
import scala.math._
import dk.bayes.dsl.infer
import dk.bayes.testutil.AssertUtil._
class GaussianProcessRegressionTest {

  @Test def test {

    val fMean = Matrix(0, 0, 0)
    val x = Matrix(1, 2, 3)
    val covFunc = CovSEiso(sf = log(7.5120), ell = log(2.1887))
    val fVar = covFunc.cov(x)
    val f = Gaussian(fMean, fVar) //f variable

    val yVar = pow(0.81075, 2) * Matrix.identity(3)
    val y = Gaussian(f, yVar, yValue = Matrix(1.0, 4, 9)) //y variable

    val fPosterior = infer(f)

    assertTrue("fPosterior mean is incorrect: " + fPosterior.m, Matrix(0.878, 4.407, 8.614).isIdentical(fPosterior.m, tol = 0.001))
    assertTrue("fPosterior diag variance is incorrect: " + fPosterior.v.extractDiag, Matrix(0.588, 0.465, 0.588).isIdentical(fPosterior.v.extractDiag, tol = 0.001))
  }
}