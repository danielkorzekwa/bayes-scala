package dk.bayes.math.gaussian.localisation1d

import org.junit._
import Assert._
import dk.bayes.math.gaussian._
import dk.bayes.math.gaussian.LinearGaussian
import dk.bayes.math.gaussian.MultivariateGaussianTest

class StaticLocalisationEPTest {

  /**
   * Expectation Propagation example.
   *
   * Variables: v,z
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

    val f0 = Gaussian(m = 3, v = 1.5)
    val f1 = LinearGaussian(a = 1, b = 0, v = 0.9)
    
    var f0_to_v = f0
    var f1_to_v = Gaussian(m = 0, v = Double.PositiveInfinity)

    //m-projection is not needed as numerator is already a Gaussian
    f0_to_v = (f0 * f1_to_v) / f1_to_v
    f1_to_v = (f1  * f0_to_v).withEvidence(1, 0.6) / f0_to_v

    val v_given_z = f0_to_v * f1_to_v

    assertEquals(1.5, v_given_z.m, 0.001)
    assertEquals(0.5625, v_given_z.v, 0.001)
  }

  /**
   * Expectation Propagation example.
   *
   * Variables: v,z1,z2
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
   *  * P(v|z1,z2) = f0_to_v * f1_to_v * f2_to_v
   *
   */
  @Test def two_observations {
    
    val f0 = Gaussian(m = 3, v = 1.5)
    val f1 = LinearGaussian(a = 1, b = 0, v = 0.9)
    val f2 = f1

    var f0_to_v = f0
    var f1_to_v = Gaussian(m = 0, v = Double.PositiveInfinity)
    var f2_to_v = Gaussian(m = 0, v = Double.PositiveInfinity)

    //m-projection is not needed as numerator is already a Gaussian
    f0_to_v = (f0 * f1_to_v * f2_to_v) / (f1_to_v * f2_to_v)
    f1_to_v = (f1 * (f0_to_v * f2_to_v)).withEvidence(1, 0.6) / (f0_to_v * f2_to_v)
    f2_to_v = (f2 * (f0_to_v * f1_to_v)).withEvidence(1, 0.6) / (f0_to_v * f1_to_v)

    val v_given_z = f0_to_v * f1_to_v * f2_to_v

    assertEquals(1.153, v_given_z.m, 0.001)
    assertEquals(0.346, v_given_z.v, 0.001)
  }
}