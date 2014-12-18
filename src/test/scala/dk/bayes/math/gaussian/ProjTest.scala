package dk.bayes.math.gaussian

import org.junit._
import Assert._

class ProjTest {

  val q = Gaussian(15, 100)

  val Z = 0.043852
  val dZ_mu = -0.001387
  val dZ_sigma = 0.00002460935
  @Test def proj {
    assertEquals(11.8370, Proj.projMu(q, Z, dZ_mu), 0.0001)
    assertEquals(101.2198, Proj.projSigma(q, Z, dZ_mu, dZ_sigma), 0.0001)
  }

}