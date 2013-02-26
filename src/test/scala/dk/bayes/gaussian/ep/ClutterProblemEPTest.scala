package dk.bayes.gaussian.ep

import org.junit._
import Assert._
import dk.bayes.gaussian.Gaussian
import dk.bayes.gaussian.LinearGaussian
import dk.bayes.gaussian.Proj

/**
 * Expectation Propagation for a Clutter Problem. Tom Minka. A family of algorithms for approximate Bayesian inference, 2001
 *
 * @author Daniel Korzekwa
 */
class ClutterProblemEPTest {

  /**
   * Expectation Propagation for a Clutter Problem.
   *
   * Variables: v:N(mu,sigma), z: (1-w)*N(v.mu,1) + w*N(0,a)
   *
   * Factors: f0: P(v), f1: P(z|v)
   *
   * Factor graph: f0 -- v -- f1 -- z
   *
   * Messages:
   * f0_to_v - Message from f0 to v
   * f1_to_v - Message from f1 to v
   *
   * P(v|z) = f0_to_v * f1_to_v
   *
   */
  @Test def single_observation {

    val f0 = Gaussian(mu = 15, sigma = 100)

    //f1
    val w = 0.4
    val a = 10
    val x = 3

    //Init messages
    var f0_to_v = f0
    var f1_to_v = Gaussian(mu = 0, sigma = Double.PositiveInfinity)

    //m-projection for f0_to_v message is not needed as numerator is already a Gaussian
    f0_to_v = (f0 * f1_to_v) / f1_to_v
    f1_to_v = project(f0_to_v, w, a, x) / f0_to_v

    val v_given_z = f0_to_v * f1_to_v

    assertEquals(11.8364, v_given_z.mu, 0.001)
    assertEquals(101.21589, v_given_z.sigma, 0.001)
  }

  /**
   * Expectation Propagation for a Clutter Problem.
   *
   * Variables: v:N(mu,sigma), z1,z2: (1-w)*N(v.mu,1) + w*N(0,a)
   *
   * Factors: f0: P(v), f1: P(z1|v), f2: P(z2|v)
   *
   * Factor graph: f0 -- v -- f1 -- z1
   *                     |
   *                     |
   *                     f2
   *                     |
   *                     |
   *                     z2
   *
   * Messages:
   * f0_to_v - Message from f0 to v
   * f1_to_v - Message from f1 to v
   * f2_to_v - Message from f2 to v
   *
   * P(v|z1,z2) = f0_to_v * f1_to_v * f2_to_v
   *
   */
  @Test def two_ovservations {
    val f0 = Gaussian(mu = 15, sigma = 100)

    //f1,f2
    val w = 0.4
    val a = 10
    val x1 = 3
    val x2 = 5

    //Init messages
    var f0_to_v = f0
    var f1_to_v = Gaussian(mu = 0, sigma = Double.PositiveInfinity)
    var f2_to_v = Gaussian(mu = 0, sigma = Double.PositiveInfinity)

    def passMessages() {
      f0_to_v = (f0 * f1_to_v * f2_to_v) / (f1_to_v * f2_to_v)
      f1_to_v = project(f0_to_v * f2_to_v, w, a, x1) / (f0_to_v * f2_to_v)
      f2_to_v = project(f0_to_v * f1_to_v, w, a, x2) / (f0_to_v * f1_to_v)
    }

    //Run EP for 20 iterations
    val v_given_z = List.fill(20) {
      passMessages()
      val v_given_z = f0_to_v * f1_to_v * f2_to_v
      v_given_z
    }

    //Iteration 1
    assertEquals(8.006, v_given_z(0).mu, 0.001)
    assertEquals(55.77, v_given_z(0).sigma, 0.001)

    //Iteration 2
    assertEquals(6.6, v_given_z(1).mu, 0.001)
    assertEquals(30.016, v_given_z(1).sigma, 0.001)

    //Iteration 20
    assertEquals(4.311, v_given_z(19).mu, 0.001)
    assertEquals(4.338, v_given_z(19).sigma, 0.001)
  }

  private def project(q: Gaussian, w: Double, a: Double, x: Double): Gaussian = {
    //Z(mu.sigma) = (1-w)*Zterm1 + w*Zterm2
    val Zterm1 = (q * LinearGaussian(1, 0, 1)).marginalise(0)
    val Zterm2 = (q * LinearGaussian(0, 0, a)).marginalise(0)

    val Z = (1 - w) * Zterm1.pdf(x) + w * Zterm2.pdf(x)
    val dZ_mu = (1 - w) * Zterm1.derivativeMu(x)
    val dZ_sigma = (1 - w) * Zterm1.derivativeSigma(x)

    val projMu = Proj.projMu(q, Z, dZ_mu)
    val projSigma = Proj.projSigma(q, Z, dZ_mu, dZ_sigma)

    Gaussian(projMu, projSigma)
  }
}