package dk.bayes.gaussian

import Math._

/**
 * Univariate Gaussian Distribution.
 * 
 * @author Daniel Korzekwa
 */
case class Gaussian(mean: Double, variance: Double) {

  def pdf(x: Double) = normConstant * exp(-pow(x - mean, 2) / (2 * variance))

  def normConstant(): Double = 1 / sqrt(2 * Pi * variance)
}