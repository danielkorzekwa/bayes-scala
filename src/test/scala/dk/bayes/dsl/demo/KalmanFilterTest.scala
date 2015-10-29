package dk.bayes.dsl.demo

import org.junit._
import org.junit.Assert._
import dk.bayes.dsl._
import dk.bayes.dsl.variable.Gaussian

class KalmanFilterTest {

  @Test def test_1d = {

    val x = Gaussian(0.5, 2)
    val y = Gaussian(x, v = 0.1, yValue = 0.7)

    val posteriorX = infer(x)
    assertEquals(0.69047, posteriorX.m, 0.0001)
    assertEquals(0.0952380, posteriorX.v, 0.0001)

  }

}