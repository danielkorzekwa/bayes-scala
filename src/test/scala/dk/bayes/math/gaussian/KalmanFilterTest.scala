package dk.bayes.math.gaussian

import org.junit.Assert._
import org.junit.Test

class KalmanFilterTest {

  /**
   * Tests for marginal y: P(y)
   */

  @Test def marginal {
    val x0 = Gaussian(3, 1.5)
    val x1Variance = 0.5

    val marginalX1 = KalmanFilter.marginal(x0, x1Variance)

    assertEquals(3, marginalX1.m, 0)
    assertEquals(2, marginalX1.v, 0)
    assertEquals(0.0297, marginalX1.pdf(0), 0.0001)
  }
  
   @Test def marginal_linear_transformation {
    val x0 = Gaussian(3, 1.5)
    val x1Variance = 0.5

    val marginalX1 = KalmanFilter.marginal(x0, A=1.5,x1Variance)

    assertEquals(4.5, marginalX1.m, 0)
    assertEquals(3.875, marginalX1.v, 0)
    assertEquals(0.0148, marginalX1.pdf(0), 0.0001)
  }

  /**
   * Tests for posterior x: P(x|z)
   */

  @Test def posterior {
    val x = Gaussian(3, 1.5)
    val zVariance = 0.5

    val marginalX = KalmanFilter.posterior(x, zVariance, 2.5)

    assertEquals(2.625, marginalX.m, 0)
    assertEquals(0.375, marginalX.v, 0)
    assertEquals(0.3869, marginalX.pdf(2), 0.0001d)
  }
}