package dk.bayes.math

import org.junit._
import Assert._

class BetaTest {

  @Test def test {
    val beta = Beta(3.2, 6.5)
    assertEquals(0.3298, beta.mean, 0.0001)
    assertEquals(0.0206, beta.variance, 0.0001)
  }
}