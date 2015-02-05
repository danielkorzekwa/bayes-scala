package dk.bayes.math.gaussian

import scala.math._
import org.apache.commons.math3.distribution.NormalDistribution
import Gaussian._
import dk.bayes.math.linear._
import breeze.stats.distributions.RandBasis
import breeze.stats.distributions.ThreadLocalRandomGenerator
import org.apache.commons.math3.random.MersenneTwister
import dk.bayes.math.numericops.NumericOps
import dk.bayes.math.gaussian.canonical.CanonicalGaussian
import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian

/**
 * Univariate Gaussian Distribution.
 *
 * @author Daniel Korzekwa
 *
 * @param m Mean
 * @param v Variance
 */
case class Gaussian(m: Double, v: Double) extends NumericOps[Gaussian] {

  require(!m.isNaN(), "Gaussian mean is NaN")
  require(!v.isNaN(), "Gaussian variance is NaN")

  private val minPrecision = 1e-7

  def draw(randSeed: Int): Double = {
    val randBasis = new RandBasis(new ThreadLocalRandomGenerator(new MersenneTwister(randSeed)))
    breeze.stats.distributions.Gaussian(m, sqrt(v))(randBasis).draw()
  }

  def draw(): Double = {
    breeze.stats.distributions.Gaussian(m, sqrt(v)).draw()
  }

  def +(x: Double): Gaussian = Gaussian(m + x, v)
  def -(x: Double): Gaussian = Gaussian(m - x, v)

  def *(x: Double): Gaussian = Gaussian(x * m, pow(x, 2) * v)

  def pdf(x: Double): Double = Gaussian.pdf(x, m, v)

  def cdf(x: Double) = Gaussian.cdf(x, m, v)

  def invcdf(x: Double) = Gaussian.invcdf(x, m, v)
  /**
   * Returns upper/lower tail gaussian truncation.
   * http://en.wikipedia.org/wiki/Truncated_normal_distribution
   *
   * @param x The value, at which gaussian is truncated
   * @param upperTail If true then upper tail truncation is returned, otherwise the lower tail is returned
   */
  def truncate(x: Double, upperTail: Boolean): Gaussian = {

    if (v.isPosInfinity) return this

    val sd = sqrt(v)

    val truncatedGaussian = upperTail match {
      case true => {
        def lambda(alpha: Double): Double = stdPdf(alpha) / (1 - stdCdf(alpha))
        def delta(alpha: Double, lambdaValue: Double): Double = lambdaValue * (lambdaValue - alpha)

        val alpha = (x - m) / sd
        val lambdaValue = lambda(alpha)

        val truncatedMean = m + sd * lambdaValue
        val truncatedVariance = v * (1 - delta(alpha, lambdaValue))

        if (!truncatedMean.isInfinity)
          Gaussian(truncatedMean, truncatedVariance)
        else Gaussian(0, Double.PositiveInfinity)
      }
      case false => {
        val beta = (x - m) / sd

        val pdfVal = stdPdf(beta)
        val cdfVal = stdCdf(beta)
        val truncatedMean = m - sd * (pdfVal / cdfVal)
        val truncatedVariance = v * (1 - beta * (pdfVal / cdfVal) - pow(pdfVal / cdfVal, 2))

        if (!truncatedMean.isInfinity)
          Gaussian(truncatedMean, truncatedVariance)
        else Gaussian(0, Double.PositiveInfinity)
      }
    }
    truncatedGaussian

  }

  def *(linearGaussian: LinearGaussian): MultivariateGaussian = {
    val m = Matrix(this.m, linearGaussian.a * this.m + linearGaussian.b)

    val R = Matrix.zeros(2, 2)
    R.set(0, 0, 1 / this.v + pow(linearGaussian.a, 2) * (1 / linearGaussian.v))
    R.set(0, 1, -linearGaussian.a * (1 / linearGaussian.v))
    R.set(1, 0, (-1 / linearGaussian.v) * linearGaussian.a)
    R.set(1, 1, 1 / linearGaussian.v)

    val v = R.inv

    MultivariateGaussian(m, v)
  }

  /**
   * Gaussian distribution of Z = XY, where X and Y are independent normal random variables
   * http://www.discuss.wmie.uz.zgora.pl/php/discuss3.php?ip=&url=pdf&nIdA=24307&nIdSesji=-1
   */
  def productXY(gaussian: Gaussian): Gaussian = {
    val productM = m * gaussian.m
    val productVar = pow(gaussian.m, 2) * v + (pow(m, 2) + v) * gaussian.v

    Gaussian(productM, productVar)
  }

  /**
   * P.A. Bromiley. Products and Convolutions of Gaussian Distributions, 2003
   */
  def +(gaussian: Gaussian): Gaussian = {
    val newM = m + gaussian.m
    val newV = v + gaussian.v
    Gaussian(newM, newV)
  }

  /**
   * http://mathworld.wolfram.com/NormalDifferenceDistribution.html
   */
  def -(gaussian: Gaussian): Gaussian = this + gaussian.copy(m = -gaussian.m)

  /**
   * Returns the derivative value of Gaussian with respect to mean, evaluated at the value of x.
   */
  def derivativeM(x: Double): Double = pdf(x) * (x - m) / v

  /**
   * Returns the derivative value of Gaussian with respect to variance, evaluated at the value of x.
   */
  def derivativeV(x: Double): Double = pdf(x) * (1d / (2 * v * v) * (x - m) * (x - m) - 1d / (2 * v))

  /**
   * Converts Gaussian to Canonical Gaussian.
   *
   */
  def toCanonical(): DenseCanonicalGaussian = DenseCanonicalGaussian(m, v)
}

object Gaussian extends GaussianNumericOps {

  private val standardNormal = new NormalDistribution(0, 1)
  /**
   * Returns the value of probability density function of the normal distribution.
   *
   * @param x The pdf function is evaluated at the value of x
   * @param m Mean
   * @param v Variance
   */
  def pdf(x: Double, m: Double, v: Double) = normConstant(v) * exp(-pow(x - m, 2) / (2 * v))

  /**
   * Returns the normalisation constant of the Gaussian distribution.
   *
   * @param v Variance
   */
  def normConstant(v: Double): Double = 1 / sqrt(2 * Pi * v)

  /**
   * Returns the value of probability density function of the standard normal distribution.
   */
  def stdPdf(x: Double): Double = pdf(x, 0, 1)

  /**
   * Returns the value of cumulative distribution function
   *
   * @param x The cdf function is evaluated at the value of x
   * @param m Mean
   * @param v Variance
   */
  def cdf(x: Double, m: Double, v: Double): Double = new NormalDistribution(m, sqrt(v)).cumulativeProbability(x)

  /**
   * Returns the value of inverse cumulative distribution function (quantile function)
   *
   * @param x The cdf function is evaluated at the value of x
   * @param m Mean
   * @param v Variance
   */
  def invcdf(x: Double, m: Double, v: Double): Double = new NormalDistribution(m, sqrt(v)).inverseCumulativeProbability(x)

  /**
   * Returns the value of cumulative distribution function of the standard normal distribution.
   */
  def stdCdf(x: Double): Double = standardNormal.cumulativeProbability(x)
  
   /**
   * Returns the value of inverse cumulative distribution function of the standard normal distribution.
   */
  def stdInvCdf(x: Double): Double = standardNormal.inverseCumulativeProbability(x)

  /**
   * Projects histogram to Gaussian distribution by matching the mean and variance moments.
   */
  def projHistogram(values: Seq[Double], probs: Seq[Double]): Gaussian = {
    require(values.size == probs.size, "Number of values is not equal to number of probabilities")

    val Z = probs.sum
    require(Z > 0, "Sum of probabilities is zero")

    val m = values.zip(probs).map { case (v, p) => v * p }.sum / Z
    val mm = values.zip(probs).map { case (v, p) => v * v * p }.sum / Z
    Gaussian(m, mm - m * m)
  }

}