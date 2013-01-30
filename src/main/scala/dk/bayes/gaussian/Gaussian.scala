package dk.bayes.gaussian

import Math._

/**
 * Univariate Gaussian Distribution.
 *
 * @author Daniel Korzekwa
 *
 * @param mu Mean
 * @param sigma Variance
 */
case class Gaussian(mu: Double, sigma: Double) {

  def pdf(x: Double) = normConstant * exp(-pow(x - mu, 2) / (2 * sigma))

  def normConstant(): Double = 1 / sqrt(2 * Pi * sigma)
}