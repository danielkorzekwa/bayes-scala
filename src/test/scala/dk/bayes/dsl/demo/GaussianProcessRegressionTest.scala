package dk.bayes.dsl.demo

import scala.math._

import org.junit._
import org.junit.Assert._

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import breeze.linalg.diag
import dk.bayes.dsl.infer
import dk.bayes.dsl.variable.Gaussian
import dk.bayes.infer.gp.cov.CovSEiso
import dk.bayes.math.linear.isIdentical
import dk.bayes.testutil.AssertUtil._
class GaussianProcessRegressionTest {

  @Test def test = {

    val fMean = DenseVector(0.0, 0, 0)
    val x = DenseMatrix(1d, 2, 3)
    val covFunc = CovSEiso(sf = log(7.5120), ell = log(2.1887))
    val fVar = covFunc.cov(x)
    val f = Gaussian(fMean, fVar) //f variable

    val yVar = pow(0.81075, 2) * DenseMatrix.eye[Double](3)
    val y = Gaussian(f, yVar, yValue = DenseVector(1.0, 4, 9)) //y variable

    val fPosterior = infer(f)

    assertTrue("fPosterior mean is incorrect: " + fPosterior.m, isIdentical(DenseVector(0.878, 4.407, 8.614), fPosterior.m, tol = 0.001))
    assertTrue("fPosterior diag variance is incorrect: " + diag(fPosterior.v), isIdentical(DenseVector(0.588, 0.465, 0.588), diag(fPosterior.v), tol = 0.001))
  }

  @Test def test_non_zero_mean = {

    val fMean = DenseVector(1d, 1, 1)
    val x = DenseMatrix(1.0, 2, 3)
    val covFunc = CovSEiso(sf = log(7.5120), ell = log(2.1887))
    val fVar = covFunc.cov(x)
    val f = Gaussian(fMean, fVar) //f variable

    val yVar = pow(0.81075, 2) * DenseMatrix.eye[Double](3)
    val y = Gaussian(f, yVar, yValue = DenseVector(1.0, 4, 9)) //y variable

    val fPosterior = infer(f)

    assertTrue("fPosterior mean is incorrect: " + fPosterior.m, isIdentical(DenseVector(0.897, 4.384, 8.633), fPosterior.m, tol = 0.001))
    assertTrue("fPosterior diag variance is incorrect: " + diag(fPosterior.v), isIdentical(DenseVector(0.588, 0.465, 0.588), diag(fPosterior.v), tol = 0.001))
  }
}