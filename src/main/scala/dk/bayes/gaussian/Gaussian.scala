package dk.bayes.gaussian

import scala.Math.Pi
import scala.Math.exp
import scala.Math.pow
import scala.Math.sqrt

import dk.bayes.gaussian.Linear.Matrix

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

  def *(linearGaussian: LinearGaussian): MultivariateGaussian = {
    val mu = Matrix(this.mu, linearGaussian.a * this.mu + linearGaussian.b)

    val R = Matrix(2, 2)
    R.set(0, 0, 1 / this.sigma + pow(linearGaussian.a, 2) * (1 / linearGaussian.sigma))
    R.set(0, 1, -linearGaussian.a * (1 / linearGaussian.sigma))
    R.set(1, 0, (-1 / linearGaussian.sigma) * linearGaussian.a)
    R.set(1, 1, 1 / linearGaussian.sigma)

    val sigma = R.inv

    MultivariateGaussian(mu, sigma)
  }

  /**
   * P.A. Bromiley. Products and Convolutions of Gaussian Distributions, 2003
   */
  def *(gaussian: Gaussian): Gaussian = {

    val product = if (gaussian.sigma == Double.PositiveInfinity) this else {
      val newMu = (mu * gaussian.sigma + gaussian.mu * sigma) / (sigma + gaussian.sigma)
      val newSigma = (sigma * gaussian.sigma) / (sigma + gaussian.sigma)
      Gaussian(newMu, newSigma)
    }

    product
  }

  /**
   * Thomas Minka. EP: A quick reference, 2008
   */
  def /(gaussian: Gaussian): Gaussian = {
    val newSigma = 1 / (1 / sigma - 1 / gaussian.sigma)
    val newMu = newSigma * (mu / sigma - gaussian.mu / gaussian.sigma)

    Gaussian(newMu, newSigma)
  }

  /**
   * P.A. Bromiley. Products and Convolutions of Gaussian Distributions, 2003
   */
  def +(gaussian: Gaussian): Gaussian = {
    val newMu = mu + gaussian.mu
    val newSigma = sigma + gaussian.sigma
    Gaussian(newMu, newSigma)
  }

  /**
   * http://mathworld.wolfram.com/NormalDifferenceDistribution.html
   */
  def -(gaussian: Gaussian): Gaussian = this + gaussian.copy(mu = -gaussian.mu)

}