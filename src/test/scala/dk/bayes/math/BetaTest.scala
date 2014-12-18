package dk.bayes.math

import org.junit._
import Assert._

class BetaTest {

  @Test def test {
    val beta = Beta(3.2, 6.5)
    assertEquals(0.3298, beta.mean, 0.0001)
    assertEquals(0.0206, beta.variance, 0.0001)

    val beta2 = Beta.fromMeanAndVariance(beta.mean, beta.variance)

    assertEquals(beta.mean, beta2.mean, 0.0001)
    assertEquals(beta.variance, beta2.variance, 0.00001)
  }
}