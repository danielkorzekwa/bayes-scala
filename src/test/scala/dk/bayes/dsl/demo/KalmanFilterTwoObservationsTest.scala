package dk.bayes.dsl.demo

import org.junit._
import Assert._
import dk.bayes.dsl._
import dk.bayes.math.gaussian.KalmanFilter
import dk.bayes.dsl.variable.Gaussian
import dk.bayes.math.linear.Matrix

class KalmanFilterTwoObservationsTest {

  @Test def test_1d {

    val x = Gaussian(3, 1.5)
    val y1 = Gaussian(x, v = 0.9, yValue = 0.6)
    val y2 = Gaussian(x, v = 0.5, yValue = 0.62)

    val posteriorX = infer(x)
    assertEquals(1.0341, posteriorX.m, 0.0001)
    assertEquals(0.2647, posteriorX.v, 0.0001)
  }

}