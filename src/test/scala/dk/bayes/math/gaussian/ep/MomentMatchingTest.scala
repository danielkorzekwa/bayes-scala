package dk.bayes.math.gaussian.ep

import org.junit._
import Assert._
import dk.bayes.math.gaussian.Gaussian
import dk.bayes.math.gaussian.LinearGaussian
import dk.bayes.math.gaussian.Proj

/**
 * Matching mean and variance moments for a distribution t(theta)*q(theta), where q(theta) ~ N(mu,sigma).
 * Tom Minka. A family of algorithms for approximate Bayesian inference, 2001
 *
 * @author Daniel Korzekwa
 */
class MomentMatchingTest {

  @Test def clutter_problem {

    val q = Gaussian(15, 100)
    //t = (1-w)*N(q.m,1) + w*N(0,a)
    val w = 0.4
    val a = 10
    val x = 3

    //Z(m,v) = (1-w)*Zterm1 + w*Zterm2

    val Zterm1 = (q * LinearGaussian(1, 0, 1)).marginalise(0)
    val Zterm2 = (q * LinearGaussian(0, 0, a)).marginalise(0)

    val Z = (1 - w) * Zterm1.pdf(x) + w * Zterm2.pdf(x)
    val dZ_m = (1 - w) * Zterm1.derivativeM(x)
    val dZ_v = (1 - w) * Zterm1.derivativeV(x)

    assertEquals(11.8364, Proj.projMu(q, Z, dZ_m), 0.0001)
    assertEquals(101.21589, Proj.projSigma(q, Z, dZ_m, dZ_v), 0.0001)
  }
}