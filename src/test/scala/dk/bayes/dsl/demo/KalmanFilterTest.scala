package dk.bayes.dsl.demo

import org.junit._
import Assert._
import dk.bayes.dsl._
import dk.bayes.math.gaussian.KalmanFilter
import dk.bayes.dsl.variable.gaussian.Gaussian
class KalmanFilterTest {

  @Test def test {

    val x = Gaussian(0.5, 2)
    val y = Gaussian(x, 0.1,value=Some(0.7))

    val posteriorX = infer(x)
    assertEquals(0.69047, posteriorX.m, 0.0001)
    assertEquals(0.0952380, posteriorX.v, 0.0001)

  }
}