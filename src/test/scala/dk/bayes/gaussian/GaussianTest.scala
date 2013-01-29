package dk.bayes.gaussian

import org.junit._
import Assert._

class GaussianTest {

  @Test def test {

    assertEquals(0.398942, Gaussian(0, 1).pdf(0), 0.0001)
    assertEquals(0.2419, Gaussian(0, 1).pdf(-1), 0.0001)

    assertEquals(0.10648, Gaussian(2, 9).pdf(0), 0.0001)
    assertEquals(0.08065, Gaussian(2, 9).pdf(-1), 0.0001)

    assertEquals(0.03707, Gaussian(1.65, 0.5).pdf(0), 0.0001)
    assertEquals(0.4607, Gaussian(1.65, 0.5).pdf(1.2), 0.0001)
  }
}