package dk.bayes.math.gaussian

import org.junit._
import Assert._
import dk.bayes.math.linear._
import dk.bayes.math.discretise.Histogram
import dk.bayes.testutil.AssertUtil._
import scala.math._

class GaussianTest {

  /**
   * Tests for constructor.
   */
  @Test(expected = classOf[IllegalArgumentException]) def constructor_variance_is_NaN {
    Gaussian(0, Double.NaN)
  }
  @Test(expected = classOf[IllegalArgumentException]) def constructor_mean_is_NaN {
    Gaussian(Double.NaN, 2)
  }

  @Test def pdf {

    assertEquals(0.398942, Gaussian(0, 1).pdf(0), 0.0001)
    assertEquals(0.2419, Gaussian(0, 1).pdf(-1), 0.0001)

    assertEquals(0.10648, Gaussian(2, 9).pdf(0), 0.0001)
    assertEquals(0.08065, Gaussian(2, 9).pdf(-1), 0.0001)

    assertEquals(0.03707, Gaussian(1.65, 0.5).pdf(0), 0.0001)
    assertEquals(0.4607, Gaussian(1.65, 0.5).pdf(1.2), 0.0001)
  }

  @Test def cdf {

    assertEquals(0.5, Gaussian(0, 1).cdf(0), 0.0001)
    assertEquals(0.6914, Gaussian(0, 1).cdf(0.5), 0.0001)
    assertEquals(0.3085, Gaussian(0, 1).cdf(-0.5), 0.0001)

    assertEquals(0.5, Gaussian(2, 3.5).cdf(2), 0.0001)
    assertEquals(0.2113, Gaussian(2, 3.5).cdf(0.5), 0.0001)
    assertEquals(0.0907, Gaussian(2, 3.5).cdf(-0.5), 0.0001)

  }

  @Test def invcdf {

    assertEquals(0, Gaussian(0, 1).invcdf(0.5), 0.0001)
    assertEquals(0.4998, Gaussian(0, 1).invcdf(0.6914), 0.0001)
    assertEquals(-0.5001, Gaussian(0, 1).invcdf(0.3085), 0.0001)

    assertEquals(2, Gaussian(2, 3.5).invcdf(0.5), 0.0001)
    assertEquals(0.4997, Gaussian(2, 3.5).invcdf(0.2113), 0.0001)
    assertEquals(-0.5002, Gaussian(2, 3.5).invcdf(0.0907), 0.0001)

  }

  @Test def truncateGaussianWithInfiniteVariance {
    assertEquals(2, Gaussian(2, Double.PositiveInfinity).truncate(0.5, true).m, 0.0001)
    assertEquals(Double.PositiveInfinity, Gaussian(2, Double.PositiveInfinity).truncate(0.5, true).v, 0.0001)
  }

  @Test def truncateUpperTail {

    assertEquals(1.141, Gaussian(0, 1).truncate(0.5, true).m, 0.0001)
    assertEquals(0.2685, Gaussian(0, 1).truncate(0.5, true).v, 0.0001)

    assertEquals(1.1284, Gaussian(0, 2).truncate(0, true).m, 0.0001)
    assertEquals(0.7267, Gaussian(0, 2).truncate(0, true).v, 0.0001)

    assertEquals(1.4647, Gaussian(0, 2).truncate(0.5, true).m, 0.0001)
    assertEquals(0.5868, Gaussian(0, 2).truncate(0.5, true).v, 0.0001)

    assertEquals(2.8217, Gaussian(2, 3.5).truncate(0.8, true).m, 0.0001)
    assertEquals(1.8386, Gaussian(2, 3.5).truncate(0.8, true).v, 0.0001)

    assertEquals(2.2610, Gaussian(2, 3.5).truncate(-0.8, true).m, 0.0001)
    assertEquals(2.7008, Gaussian(2, 3.5).truncate(-0.8, true).v, 0.0001)

    assertEquals(0, Gaussian(-0.009449899053606714, 1.2107926713467165E-6).truncate(0, true).m, 0.0001)
    assertEquals(Double.PositiveInfinity, Gaussian(-0.009449899053606714, 1.2107926713467165E-6).truncate(0, true).v, 0.0001)

  }

  @Test def truncateLowerTail {
    assertEquals(-0.50917, Gaussian(0, 1).truncate(0.5, false).m, 0.0001)
    assertEquals(0.48617, Gaussian(0, 1).truncate(0.5, false).v, 0.0001)

    assertEquals(-1.1283, Gaussian(0, 2).truncate(0, false).m, 0.0001)
    assertEquals(0.72674, Gaussian(0, 2).truncate(0, false).v, 0.0001)

    assertEquals(-0.8305, Gaussian(0, 2).truncate(0.5, false).m, 0.0001)
    assertEquals(0.8949, Gaussian(0, 2).truncate(0.5, false).v, 0.0001)

    assertEquals(-0.33126, Gaussian(2, 3.5).truncate(0.8, false).m, 0.0001)
    assertEquals(0.8627, Gaussian(2, 3.5).truncate(0.8, false).v, 0.0001)

    assertEquals(-1.6216, Gaussian(2, 3.5).truncate(-0.8, false).m, 0.0001)
    assertEquals(0.5243, Gaussian(2, 3.5).truncate(-0.8, false).v, 0.0001)

    assertEquals(0, Gaussian(0.1, 0.0001).truncate(0, false).m, 0.0001)
    assertEquals(Double.PositiveInfinity, Gaussian(0.1, 0.0001).truncate(0, false).v, 0.0001)

  }

  @Test def product_with_linear_gaussian {

    val priorProb = Gaussian(3, 1.5)
    val likelihoodProb = LinearGaussian(-0.1, 2, 0.5)

    val jointProb = priorProb * likelihoodProb

    assertEquals(Matrix(Array(3, 1.7)).toString(), jointProb.m.toString())
    assertEquals(Matrix(2, 2, Array(1.5, -0.15, -0.15, 0.515)).toString(), jointProb.v.toString())

    assertEquals(0.0111, jointProb.pdf(Matrix(Array(3.5, 0))), 0.0001d)
    assertEquals(0.1679, jointProb.pdf(Matrix(Array(3d, 2))), 0.0001d)
  }

  @Test def product_of_different_gaussians {

    val gaussian1 = Gaussian(2, 0.5)
    val gaussian2 = Gaussian(3, 0.2)

    val product = gaussian1 * gaussian2

    assertEquals(2.7142, product.m, 0.0001)
    assertEquals(0.1428, product.v, 0.0001)
  }

  @Test def product_of_single_gaussian {

    val gaussian1 = Gaussian(2, 0.5)

    val product = gaussian1 * gaussian1

    assertEquals(2, product.m, 0.0001)
    assertEquals(0.25, product.v, 0.0001)
  }

  @Test def product_gaussian_with_infinite_variance {

    val gaussian1 = Gaussian(2, 0.5)
    val gaussian2 = Gaussian(3, Double.PositiveInfinity)

    val product = gaussian1 * gaussian2

    assertEquals(2, product.m, 0.0001)
    assertEquals(0.5, product.v, 0.0001)

    val product2 = gaussian2 * gaussian1

    assertEquals(2, product2.m, 0.0001)
    assertEquals(0.5, product2.v, 0.0001)
  }

  @Test def divide_negative_variance {
    val gaussian1 = Gaussian(2, 0.5)
    val gaussian2 = Gaussian(3, 0.2)

    val div = gaussian1 / gaussian2

    assertEquals(3.6666, div.m, 0.0001)
    assertEquals(-0.3333, div.v, 0.0001)
  }

  @Test def divide {
    val gaussian1 = Gaussian(2, 0.5)
    val gaussian2 = Gaussian(3, 0.2)

    val div = gaussian2 / gaussian1

    assertEquals(3.6666, div.m, 0.0001)
    assertEquals(0.3333, div.v, 0.0001)
  }

  @Test def divide_single_gaussian {
    val gaussian1 = Gaussian(2, 0.5)

    val div = gaussian1 / gaussian1

    assertEquals(0, div.m, 0.0001)
    assertEquals(Double.PositiveInfinity, div.v, 0.0001)
  }

  @Test def divide_with_infinite_gaussian {
    val gaussian1 = Gaussian(3, Double.PositiveInfinity)
    val gaussian2 = Gaussian(2, 0.5)

    val div = gaussian2 / gaussian1

    assertEquals(2, div.m, 0.0001)
    assertEquals(0.5, div.v, 0.0001)

    val div2 = gaussian1 / gaussian2

    assertEquals(3, div2.m, 0.0001)
    assertEquals(Double.PositiveInfinity, div2.v, 0.0001)
  }

  @Test def product_then_divide {
    val product = (Gaussian(4.000, 98.361) - Gaussian(41.000, 42.361)) * Gaussian(6.150, 10.096)
    val divide = product / Gaussian(6.150, 10.096)

    assertGaussian(Gaussian(-37, 140.722), divide, 0.0001)

  }

  @Test def add {

    val gaussian1 = Gaussian(2, 0.5)
    val gaussian2 = Gaussian(3, 0.2)

    val sum = gaussian1 + gaussian2

    assertEquals(5, sum.m, 0.0001)
    assertEquals(0.7, sum.v, 0.0001)
  }

  @Test def multiply_by_a_constant {
    val gaussian = Gaussian(0.3, pow(0.8, 2))

    val multGaussian = gaussian * 4
    assertEquals(1.2, multGaussian.m, 0.0001)
    assertEquals(10.24, multGaussian.v, 0.0001)
  }

  @Test def productXY {

    val x1 = Gaussian(0.3, pow(0.8, 2))
    val x2 = Gaussian(0.5, pow(0.6, 2))
    val x3 = Gaussian(0.8, pow(0.2, 2))

    val x1x2 = x1.productXY(x2)
    val x2x1 = x2.productXY(x1)
    val x1x2x3 = x1.productXY(x2).productXY(x3)

    assertEquals(0.15, x1x2.m, 0.0001)
    assertEquals(0.4228, x1x2.v, 0.0001)

    assertEquals(0.15, x2x1.m, 0.0001)
    assertEquals(0.4228, x2x1.v, 0.0001)

    assertEquals(0.12, x1x2x3.m, 0.0001)
    assertEquals(0.2884, x1x2x3.v, 0.0001)
  }

  @Test def subtract {

    val gaussian1 = Gaussian(2, 0.5)
    val gaussian2 = Gaussian(3, 0.2)

    val diff = gaussian1 - gaussian2

    assertEquals(-1, diff.m, 0.0001)
    assertEquals(0.7, diff.v, 0.0001)
  }

  @Test def derivativeMu {
    assertEquals(-0.0023121, Gaussian(15, 101).derivativeM(3), 0.000001)
    assertEquals(0, Gaussian(15, 101).derivativeM(15), 0)

    assertEquals(0.1079, Gaussian(0, 1).derivativeM(2), 0.0001)
  }

  @Test def derivativeSigma {
    assertEquals(0.000041015595, Gaussian(15, 101).derivativeV(3), 0.00000001)

    assertEquals(0.0809, Gaussian(0, 1).derivativeV(2), 0.0001)
  }

  @Test(expected = classOf[IllegalArgumentException]) def projHistogram_inconsistent_values_with_probs {
    Gaussian.projHistogram(List(1, 2, 3), List(0.2, 0.3, 0.3, 0.2))
  }

  @Test(expected = classOf[IllegalArgumentException]) def projHistogram_zero_probs {
    Gaussian.projHistogram(List(1, 2, 3), List(0, 0, 0))
  }

  @Test def projHistogram {

    val gaussian = Gaussian(10, 2)
    val histogram = Histogram(gaussian.m - 4 * gaussian.v, gaussian.m + 4 * gaussian.v, 50)

    val proj = Gaussian.projHistogram(histogram.toValues, histogram.mapValues(v => gaussian.pdf(v)))

    assertGaussian(Gaussian(10, 2), proj, 0.0001)
  }
}