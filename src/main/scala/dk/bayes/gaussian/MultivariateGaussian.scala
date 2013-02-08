package dk.bayes.gaussian

import dk.bayes.gaussian.Linear._
import scala.Math._

/**
 * Multivariate Gaussian from the book 'Christopher M. Bishop. Pattern Recognition and Machine Learning (Information Science and Statistics), 2009'
 *
 * @author Daniel Korzekwa
 */
case class MultivariateGaussian(mu: Matrix, sigma: Matrix) {

  /**
   * Returns Gaussian, marginalising out given variable
   *
   * @param varIndex Index of a variable, which is marginalised out
   */
  def marginalise(varIndex: Int): MultivariateGaussian = {

    val marginalMu = mu.filterNotRow(varIndex)
    val marginalSigma = sigma.filterNot(varIndex, varIndex)

    MultivariateGaussian(marginalMu, marginalSigma)
  }

  def withEvidence(varIndex: Int, value: Double): MultivariateGaussian = {

    val precision = sigma.inv
    val precisionAA = precision.filterNot(varIndex, varIndex)
    val precisionAB = precision.column(varIndex).filterNotRow(varIndex)

    val muA = mu.filterNotRow(varIndex)

    val marginalMu = muA - precisionAA.inv * precisionAB * (value - mu(varIndex))
    val marginalSigma = precisionAA.inv

    MultivariateGaussian(marginalMu, marginalSigma)
  }

  def *(linearGaussian: LinearGaussian): MultivariateGaussian = toGaussian() * linearGaussian

  def toGaussian(): Gaussian = {
    require(mu.size == 1 && sigma.size == 1, "Multivariate gaussian cannot be transformed into univariate gaussian")
    Gaussian(mu.at(0), sigma.at(0))
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
    val pdfValue = p * exp(-0.5 * ((x - mu).transpose * sigma.inv * (x - mu)).at(0))
    pdfValue
  }

  def normConstant(): Double = 1d / (pow(2 * Pi, mu.size.toDouble / 2) * sqrt(sigma.det))
}

object MultivariateGaussian {

  def apply(mu: Double, sigma: Double): MultivariateGaussian = new MultivariateGaussian(Matrix(mu), Matrix(sigma))
}
