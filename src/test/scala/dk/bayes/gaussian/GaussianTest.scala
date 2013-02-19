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

  @Test def product_with_linear_gaussian {

    val priorProb = Gaussian(3, 1.5)
    val likelihoodProb = LinearGaussian(-0.1, 2, 0.5)

    val jointProb = priorProb * likelihoodProb

    assertEquals(Matrix(Array(3, 1.7)).toString(), jointProb.mu.toString())
    assertEquals(Matrix(2, 2, Array(1.5, -0.15, -0.15, 0.515)).toString(), jointProb.sigma.toString())

    assertEquals(0.0111, jointProb.pdf(Matrix(Array(3.5, 0))), 0.0001d)
    assertEquals(0.1679, jointProb.pdf(Matrix(Array(3d, 2))), 0.0001d)
  }

  @Test def product_of_different_gaussians {

    val gaussian1 = Gaussian(2, 0.5)
    val gaussian2 = Gaussian(3, 0.2)

    val product = gaussian1 * gaussian2

    assertEquals(2.7142, product.mu, 0.0001)
    assertEquals(0.1428, product.sigma, 0.0001)
  }

  @Test def product_of_single_gaussian {

    val gaussian1 = Gaussian(2, 0.5)

    val product = gaussian1 * gaussian1

    assertEquals(2, product.mu, 0.0001)
    assertEquals(0.25, product.sigma, 0.0001)
  }
  
   @Test def product_gaussian_with_infinite_variance {

    val gaussian1 = Gaussian(2, 0.5)
    val gaussian2 = Gaussian(3, Double.PositiveInfinity)

    val product = gaussian1 * gaussian2

    assertEquals(2, product.mu, 0.0001)
    assertEquals(0.5, product.sigma, 0.0001)
  }

  @Test def divide_negative_variance {
    val gaussian1 = Gaussian(2, 0.5)
    val gaussian2 = Gaussian(3, 0.2)

    val div = gaussian1 / gaussian2

    assertEquals(3.6666, div.mu, 0.0001)
    assertEquals(-0.3333, div.sigma, 0.0001)
  }

  @Test def divide {
    val gaussian1 = Gaussian(2, 0.5)
    val gaussian2 = Gaussian(3, 0.2)

    val div = gaussian2 / gaussian1

    assertEquals(3.6666, div.mu, 0.0001)
    assertEquals(0.3333, div.sigma, 0.0001)
  }

  @Test def divide_single_gaussian {
    val gaussian1 = Gaussian(2, 0.5)

    val div = gaussian1 / gaussian1

    assertEquals(Double.NaN, div.mu, 0.0001)
    assertEquals(Double.PositiveInfinity, div.sigma, 0.0001)
  }

  @Test def add {

    val gaussian1 = Gaussian(2, 0.5)
    val gaussian2 = Gaussian(3, 0.2)

    val sum = gaussian1 + gaussian2

    assertEquals(5, sum.mu, 0.0001)
    assertEquals(0.7, sum.sigma, 0.0001)
  }

  @Test def subtract {

    val gaussian1 = Gaussian(2, 0.5)
    val gaussian2 = Gaussian(3, 0.2)

    val diff = gaussian1 - gaussian2

    assertEquals(-1, diff.mu, 0.0001)
    assertEquals(0.7, diff.sigma, 0.0001)
  }
}