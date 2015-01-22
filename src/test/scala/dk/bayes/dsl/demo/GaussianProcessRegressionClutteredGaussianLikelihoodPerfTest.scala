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
import dk.bayes.math.numericops.NumericOps
import dk.bayes.math.numericops.multOp
import dk.bayes.dsl.factor.SingleFactor
import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian
import dk.bayes.dsl.factor.DoubleFactor
import dk.bayes.infer.epnaivebayes.inferPosterior
import dk.bayes.math.gaussian.canonical.SparseCanonicalGaussian
import dk.bayes.math.gaussian.canonical.CanonicalGaussian
import dk.bayes.math.numericops._
import dk.bayes.math.gaussian.canonical.SparseCanonicalGaussian
import dk.bayes.math.gaussian.canonical.SparseCanonicalGaussian
import dk.bayes.math.gaussian.canonical.SparseCanonicalGaussian
import dk.bayes.math.gaussian.canonical.SparseCanonicalGaussian
import dk.bayes.math.gaussian.canonical.SparseCanonicalGaussian
import dk.bayes.math.gaussian.canonical.SparseCanonicalGaussian

class GaussianProcessRegressionClutteredGaussianLikelihoodPerfTest {

  @Test def test {

    val n = 400

    val fMean = Matrix.zeros(n, 1)

    val x = Matrix((1d to n by 1).toArray)
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