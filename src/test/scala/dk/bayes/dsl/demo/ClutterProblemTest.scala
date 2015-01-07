package dk.bayes.dsl.demo

import org.junit._
import Assert._
import dk.bayes.dsl.variable.Gaussian
import ClutterProblemTest._
import dk.bayes.dsl.infer
import dk.bayes.dsl.Variable
import dk.bayes.dsl.variable.gaussian.UnivariateLinearGaussian
import dk.bayes.dsl.variable.gaussian.UnivariateGaussian
import dk.bayes.dsl.factor.DoubleFactor
import dk.bayes.math.gaussian.CanonicalGaussian
import dk.bayes.math.gaussian.Proj
import dk.bayes.math.gaussian.LinearGaussian

/**
 * References for the Clutter problem:
 * - Tom Minka thesis (http://research.microsoft.com/en-us/um/people/minka/papers/ep/minka-thesis.pdf)
 *  - Christopher M. Bishop. Pattern Recognition and Machine Learning (Information Science and Statistics), 2009, chapter 10.7.1, page 511
 */
class ClutterProblemTest {

  @Test def test {

    val x = Gaussian(15, 100)
    val y1 = ClutteredGaussian(x, w = 0.4, a = 10, value = 3)
    val y2 = ClutteredGaussian(x, w = 0.4, a = 10, value = 5)

    val posteriorX = infer(x)
    assertEquals(4.3431, posteriorX.m, 0.0001)
    assertEquals(4.3163, posteriorX.v, 0.0001)

  }
}

object ClutterProblemTest {

  /**
   * cluttered_gaussian ~ (1-w)*N(v.m,1) + w*N(0,a)
   */
  case class ClutteredGaussian(x: UnivariateGaussian, w: Double, a: Double, value: Double) extends Variable with ClutteredGaussianFactor {
    def getParents(): Seq[Variable] = Vector(x)

  }

  trait ClutteredGaussianFactor extends DoubleFactor[UnivariateGaussian, Any] {
    val w: Double
    val a: Double
    val value: Double

    def marginals(x: Option[UnivariateGaussian], y: Option[Any]): (Option[UnivariateGaussian], Option[Any]) = {

      val posterior = project(dk.bayes.math.gaussian.Gaussian(x.get.m, x.get.v), w, a, value)
      val posteriorVar = new UnivariateGaussian(posterior.m, posterior.v)
      (Some(posteriorVar), None)

    }

    private def project(q: dk.bayes.math.gaussian.Gaussian, w: Double, a: Double, x: Double): dk.bayes.math.gaussian.Gaussian = {
      //Z(m,v) = (1-w)*Zterm1 + w*Zterm2
      val Zterm1 = (q * LinearGaussian(1, 0, 1)).marginalise(0)
      val Zterm2 = (q * LinearGaussian(0, 0, a)).marginalise(0)

      val Z = (1 - w) * Zterm1.pdf(x) + w * Zterm2.pdf(x)
      val dZ_m = (1 - w) * Zterm1.derivativeM(x)
      val dZ_v = (1 - w) * Zterm1.derivativeV(x)

      val projM = Proj.projMu(q, Z, dZ_m)
      val projV = Proj.projSigma(q, Z, dZ_m, dZ_v)

      dk.bayes.math.gaussian.Gaussian(projM, projV)
    }
  }
}