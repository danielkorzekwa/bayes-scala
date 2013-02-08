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
}