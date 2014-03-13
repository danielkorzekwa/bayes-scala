package dk.bayes.math

import scala.math._

/**
 * Beta distribution.
 *
 * http://en.wikipedia.org/wiki/Beta_distribution
 *
 */
case class Beta(alpha: Double, beta: Double) {

  def mean(): Double = alpha / (alpha + beta)
  def variance(): Double = alpha * beta / (pow(alpha + beta, 2) * (alpha + beta + 1))
}