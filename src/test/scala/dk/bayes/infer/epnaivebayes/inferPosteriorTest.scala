package dk.bayes.infer.epnaivebayes

import org.junit._
import Assert._
import dk.bayes.math.gaussian.Gaussian
import dk.bayes.math.gaussian.LinearGaussian
import scala.math._
import dk.bayes.math.gaussian.CanonicalGaussian
import dk.bayes.math.linear.Matrix
import inferPosteriorTest._

class inferPosteriorTest {

  @Test def test_1d_kalman_single_observation {
    val prior = Gaussian(0.5, 2)
    val likelihoods = List(0.7)
    val epBayesianBet = new GaussianEPBayesianNet(prior, likelihoods, emissionVar = 0.1)

    val posterior = inferPosterior(epBayesianBet)

    assertEquals(0.69047, posterior.m, 0.0001)
    assertEquals(0.0952380, posterior.v, 0.0001)
  }

  @Test def test_1d_kalman_two_observations {
    val prior = Gaussian(3, 1.5)
    val likelihoods = List(0.6, 0.62)
    val epBayesianBet = new GaussianEPBayesianNet(prior, likelihoods, emissionVar = 0.9)

    val posterior = inferPosterior(epBayesianBet)

    assertEquals(1.1615, posterior.m, 0.0001)
    assertEquals(0.3461, posterior.v, 0.0001)
  }

}

object inferPosteriorTest {
  class GaussianEPBayesianNet(val prior: Gaussian, val likelihoods: Seq[Double], emissionVar: Double) extends EPBayesianNet[Gaussian, Double] {
    val initFactorMsgUp = Gaussian(0, 1)

    def product(x1: Gaussian, x2: Gaussian): Gaussian = x1 * x2

    def divide(x1: Gaussian, x2: Gaussian): Gaussian = x1 / x2

    def marginalX(x: Gaussian, y: Double): Gaussian = {
      val xCanon = CanonicalGaussian(x.m, x.v)
      val yCanon = CanonicalGaussian(a = Matrix(1), b = 0, v = emissionVar)
      val marginal = (xCanon.extend(2, 0) * yCanon).withEvidence(1, y)
      marginal.toGaussian
    }

    def isIdentical(x1: Gaussian, x2: Gaussian, threshold: Double): Boolean = {
      abs(x1.m - x2.m) < threshold &&
        abs(x1.v - x2.v) < threshold &&
        x1.v > 0 && x2.v > 0
    }
  }
}
