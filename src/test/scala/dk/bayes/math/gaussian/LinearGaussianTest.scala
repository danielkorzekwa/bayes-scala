package dk.bayes.math.gaussian

import org.junit._
import Assert._
import dk.bayes.math.linear._

class LinearGaussianTest {

  @Test def product {

    val priorProb = Gaussian(3, 1.5)
    val likelihoodProb = LinearGaussian(-0.1, 2, 0.5)

    val jointProb = likelihoodProb * priorProb

    assertEquals(Matrix(Array(3, 1.7)).toString(), jointProb.m.toString())
    assertEquals(Matrix(2, 2, Array(1.5, -0.15, -0.15, 0.515)).toString(), jointProb.v.toString())

    assertEquals(0.0111, jointProb.pdf(Matrix(Array(3.5, 0))), 0.0001d)
    assertEquals(0.1679, jointProb.pdf(Matrix(Array(3d, 2))), 0.0001d)
  }
}