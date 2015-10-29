package dk.bayes.math.gaussian

import scala.math.Pi

import org.apache.commons.math3.random.MersenneTwister

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import breeze.linalg.cholesky
import breeze.linalg.det
import breeze.linalg.inv
import breeze.numerics._
import breeze.stats.distributions.Rand
import breeze.stats.distributions.RandBasis
import breeze.stats.distributions.ThreadLocalRandomGenerator
import dk.bayes.math.linear._
import scala.language.implicitConversions

/**
 * Multivariate Gaussian from the book 'Christopher M. Bishop. Pattern Recognition and Machine Learning (Information Science and Statistics), 2009'
 *
 * @author Daniel Korzekwa
 */
case class MultivariateGaussian(m: DenseVector[Double], v: DenseMatrix[Double]) {

  def draw(randSeed: Int): Array[Double] = {

    val randBasis = new RandBasis(new ThreadLocalRandomGenerator(new MersenneTwister(randSeed)))
    val root: DenseMatrix[Double] = cholesky(v)
    val z: DenseVector[Double] = DenseVector.rand(m.length, randBasis.gaussian(0, 1))

    val sample = { root * z += m }
    sample.data
  }

  /**
   * Returns Gaussian marginal for a random variable at a given position
   */
  def marginal(varIndex: Int): Gaussian = {
    val mean = m(varIndex)
    val variance = v(varIndex, varIndex)

    Gaussian(mean, variance)
  }

  /**
   * Returns Gaussian, marginalising out given variable
   *
   * @param varIndex Index of a variable, which is marginalised out
   */
  def marginalise(varIndex: Int): MultivariateGaussian = {

    val marginalMean = filterNotRow(m, varIndex)
    val marginalVariance = filterNot(v, varIndex, varIndex)

    MultivariateGaussian(marginalMean, marginalVariance)
  }

  def withEvidence(varIndex: Int, value: Double): MultivariateGaussian = {

    val precision = invchol(cholesky(v).t)
    val precisionAA = filterNot(precision, varIndex, varIndex)
    val precisionAB = filterNotRow(precision(::,varIndex),varIndex)

    val meanA = filterNotRow(m, varIndex)

    val precisionAAinv = invchol(cholesky(precisionAA).t)
    
    val marginalMean = meanA - precisionAAinv * precisionAB * (value - m(varIndex))
    val marginalVariance = precisionAAinv

    MultivariateGaussian(marginalMean, marginalVariance)
  }

  def *(linearGaussian: LinearGaussian): MultivariateGaussian = toGaussian() * linearGaussian

  def toGaussian(): Gaussian = {
    require(m.size == 1 && v.size == 1, "Multivariate gaussian cannot be transformed into univariate gaussian")
    Gaussian(m(0), v(0,0))
  }

  /**
   * Returns the value of probability density function for a given value of x.
   */
  def pdf(x: Double): Double = pdf(DenseVector(x))

  /**
   * Returns the value of probability density function for a given value of vector x.
   */
  def pdf(x: DenseVector[Double]): Double = {
    val p = normConstant()
    val pdfValue = p * exp(-0.5 * ((x - m).t * invchol(cholesky(v).t) * (x - m)))
    pdfValue
  }

  def normConstant(): Double = 1d / (pow(2 * Pi, m.size.toDouble / 2) * sqrt(det(v)))
}

object MultivariateGaussian {

  implicit def toGaussian(mvnGaussian: MultivariateGaussian): Gaussian = mvnGaussian.toGaussian()

  /**
   * @param m Mean
   * @param v Variance
   */
  def apply(m: Double, v: Double): MultivariateGaussian = new MultivariateGaussian(DenseVector(m), DenseMatrix(v))
}
