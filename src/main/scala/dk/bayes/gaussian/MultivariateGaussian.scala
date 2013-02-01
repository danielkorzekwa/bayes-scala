package dk.bayes.gaussian

import dk.bayes.gaussian.Linear._

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
}
