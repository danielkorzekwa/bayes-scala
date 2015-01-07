package dk.bayes.math.gaussian.ep

import org.junit._
import Assert._
import dk.bayes.math.gaussian.Gaussian
import dk.bayes.math.gaussian.LinearGaussian
import dk.bayes.math.gaussian.Proj

/**
 * Expectation Propagation for the Clutter Problem. Tom Minka. A family of algorithms for approximate Bayesian inference, 2001
 *
 * @author Daniel Korzekwa
 */
class ClutterProblemEPTest {

  /**
   * Expectation Propagation for the Clutter Problem.
   *
   * Variables: v:N(m,variance), z: (1-w)*N(v.m,1) + w*N(0,a)
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

    val f0 = Gaussian(m = 15, v = 100)

    //f1
    val w = 0.4
    val a = 10
    val x = 3

    //Init messages
    var f0_to_v = f0
    var f1_to_v = Gaussian(m = 0, v = Double.PositiveInfinity)

    //m-projection for f0_to_v message is not needed as numerator is already a Gaussian
    f0_to_v = (f0 * f1_to_v) / f1_to_v
    f1_to_v = project(f0_to_v, w, a, x) / f0_to_v

    val v_given_z = f0_to_v * f1_to_v

    assertEquals(11.8364, v_given_z.m, 0.001)
    assertEquals(101.21589, v_given_z.v, 0.001)
  }

  /**
   * Expectation Propagation for the Clutter Problem.
   *
   * Variables: v:N(m,variance), z1,z2: (1-w)*N(v.m,1) + w*N(0,a)
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
    val f0 = Gaussian(m = 15, v = 100)

    //f1,f2
    val w = 0.4
    val a = 10
    val x1 = 3
    val x2 = 5

    //Init messages
    var f0_to_v = f0
    var f1_to_v = Gaussian(m = 0, v = Double.PositiveInfinity)
    var f2_to_v = Gaussian(m = 0, v = Double.PositiveInfinity)

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
    assertEquals(8.006, v_given_z(0).m, 0.001)
    assertEquals(55.77, v_given_z(0).v, 0.001)

    //Iteration 2
    assertEquals(6.6, v_given_z(1).m, 0.001)
    assertEquals(30.016, v_given_z(1).v, 0.001)

    //Iteration 20
    assertEquals(4.311, v_given_z(19).m, 0.001)
    assertEquals(4.338, v_given_z(19).v, 0.001)
  }

  private def project(q: Gaussian, w: Double, a: Double, x: Double): Gaussian = {
    //Z(m,v) = (1-w)*Zterm1 + w*Zterm2
    val Zterm1 = (q * LinearGaussian(1, 0, 1)).marginalise(0)
    val Zterm2 = (q * LinearGaussian(0, 0, a)).marginalise(0)

    val Z = (1 - w) * Zterm1.pdf(x) + w * Zterm2.pdf(x)
    val dZ_m = (1 - w) * Zterm1.derivativeM(x)
    val dZ_v = (1 - w) * Zterm1.derivativeV(x)

    val projM = Proj.projMu(q, Z, dZ_m)
    val projV = Proj.projSigma(q, Z, dZ_m, dZ_v)

    println( Gaussian(projM, projV))
    Gaussian(projM, projV)
  }
}