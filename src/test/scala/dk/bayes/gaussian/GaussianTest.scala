package dk.bayes.gaussian

import org.junit._
import Assert._
import dk.bayes.gaussian.Linear._
class GaussianTest {

  @Test def pdf {

    assertEquals(0.398942, Gaussian(0, 1).pdf(0), 0.0001)
    assertEquals(0.2419, Gaussian(0, 1).pdf(-1), 0.0001)

    assertEquals(0.10648, Gaussian(2, 9).pdf(0), 0.0001)
    assertEquals(0.08065, Gaussian(2, 9).pdf(-1), 0.0001)

    assertEquals(0.03707, Gaussian(1.65, 0.5).pdf(0), 0.0001)
    assertEquals(0.4607, Gaussian(1.65, 0.5).pdf(1.2), 0.0001)
  }

  @Test def product {

    val priorProb = Gaussian(3, 1.5)
    val likelihoodProb = LinearGaussian(-0.1, 2, 0.5)

    val jointProb = priorProb * likelihoodProb

    assertEquals(Matrix(Array(3, 1.7)).toString(), jointProb.mu.toString())
    assertEquals(Matrix(2, 2, Array(1.5, -0.15, -0.15, 0.515)).toString(), jointProb.sigma.toString())

    assertEquals(0.0111, jointProb.pdf(Matrix(Array(3.5, 0))), 0.0001d)
    assertEquals(0.1679, jointProb.pdf(Matrix(Array(3d, 2))), 0.0001d)
  }
}