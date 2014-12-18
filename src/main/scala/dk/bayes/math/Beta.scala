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

object Beta {
  
  def fromMeanAndVariance(m: Double, v: Double): Beta = {
    val alpha = ((1 - m) / v - 1d / m) * (m * m)
    val beta = alpha * (1d / m - 1)

    new Beta(alpha, beta)
  }
}