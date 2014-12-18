package dk.bayes.math.gaussian

import org.junit._
import Assert._

class MoGTest {

  @Test def test {
    val mog = MoG(z=Array(0.6,0.4),x=Array(Gaussian(0.5,2),Gaussian(0.7,3)))
    
    val approx = mog.gaussianApprox
    assertEquals(0.58,approx.m,0.0001)
    assertEquals(2.4095,approx.v,0.0001)
  }
}