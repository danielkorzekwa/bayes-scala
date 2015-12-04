package dk.bayes.dsl.demo

import scala.math._

import org.junit._
import org.junit.Assert._

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import dk.bayes.dsl.demo.variables.ClutteredGaussian
import dk.bayes.dsl.infer
import dk.bayes.dsl.variable.Gaussian
import dk.bayes.infer.gp.cov.CovSEiso
import dk.bayes.clustergraph.testutil.AssertUtil._

class GaussianProcessRegressionClutteredGaussianLikelihoodPerfTest {

  @Test def test:Unit = {

    val n = 400

    val fMean = DenseVector.zeros[Double](n)

    val x = DenseMatrix((1d to n by 1)).t
    val covFunc = CovSEiso(sf = log(7.5120), ell = log(2.1887))
    val fVar = covFunc.cov(x)
    val f = Gaussian(fMean, fVar) //f variable

    for (i <- 0 until n) {
      ClutteredGaussian(x = f, xIndex = i, w = 0.4, a = 10, value = i / 2)
    }
    val fPosterior = infer(f)

    assertEquals(-0.121, fPosterior.m(0), 0.001)
    assertEquals(0.724, fPosterior.m(2), 0.001)
    assertEquals(1.752, fPosterior.m(4), 0.001)

    assertEquals(4.538, fPosterior.v(0, 0), 0.001)
    assertEquals(1.116, fPosterior.v(2, 2), 0.001)
    assertEquals(0.858, fPosterior.v(4, 4), 0.001)

  }
}