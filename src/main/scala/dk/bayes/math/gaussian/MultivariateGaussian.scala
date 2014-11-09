package dk.bayes.math.gaussian

import dk.bayes.math.linear._
import scala.math._
import breeze.linalg.DenseVector
import breeze.stats.distributions.Rand
import breeze.linalg.DenseMatrix
import breeze.stats.distributions.RandBasis
import breeze.stats.distributions.ThreadLocalRandomGenerator
import org.apache.commons.math3.random.{ MersenneTwister, RandomGenerator }
import breeze.linalg.cholesky

/**
 * Multivariate Gaussian from the book 'Christopher M. Bishop. Pattern Recognition and Machine Learning (Information Science and Statistics), 2009'
 *
 * @author Daniel Korzekwa
 */
case class MultivariateGaussian(m: Matrix, v: Matrix) {

  def draw(randSeed: Int): Array[Double] = {

    val mean = DenseVector(m.toArray)
    val variance = DenseMatrix(v.toArray).reshape(v.numRows, v.numCols)
    val randBasis = new RandBasis(new ThreadLocalRandomGenerator(new MersenneTwister(randSeed)))
    val root: DenseMatrix[Double] = cholesky(variance)
    val z: DenseVector[Double] = DenseVector.rand(mean.length, randBasis.gaussian(0, 1))

    val sample = { root * z += mean }
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

    val marginalMean = m.filterNotRow(varIndex)
    val marginalVariance = v.filterNot(varIndex, varIndex)

    MultivariateGaussian(marginalMean, marginalVariance)
  }

  def withEvidence(varIndex: Int, value: Double): MultivariateGaussian = {

    val precision = v.inv
    val precisionAA = precision.filterNot(varIndex, varIndex)
    val precisionAB = precision.column(varIndex).filterNotRow(varIndex)

    val meanA = m.filterNotRow(varIndex)

    val marginalMean = meanA - precisionAA.inv * precisionAB * (value - m(varIndex))
    val marginalVariance = precisionAA.inv

    MultivariateGaussian(marginalMean, marginalVariance)
  }

  def *(linearGaussian: LinearGaussian): MultivariateGaussian = toGaussian() * linearGaussian

  def toGaussian(): Gaussian = {
    require(m.size == 1 && v.size == 1, "Multivariate gaussian cannot be transformed into univariate gaussian")
    Gaussian(m.at(0), v.at(0))
  }

  /**
   * Returns the value of probability density function for a given value of x.
   */
  def pdf(x: Double): Double = pdf(Matrix(x))

  /**
   * Returns the value of probability density function for a given value of vector x.
   */
  def pdf(x: Matrix): Double = {
    val p = normConstant()
    val pdfValue = p * exp(-0.5 * ((x - m).transpose * v.inv * (x - m)).at(0))
    pdfValue
  }

  def normConstant(): Double = 1d / (pow(2 * Pi, m.size.toDouble / 2) * sqrt(v.det))
}

object MultivariateGaussian {

  implicit def toGaussian(mvnGaussian: MultivariateGaussian): Gaussian = mvnGaussian.toGaussian()

  /**
   * @param m Mean
   * @param v Variance
   */
  def apply(m: Double, v: Double): MultivariateGaussian = new MultivariateGaussian(Matrix(m), Matrix(v))
}
