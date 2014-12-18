package dk.bayes.math.gaussian

import scala.math._
/**
 * Mixture of Gaussians.
 *
 * Math details:
 * http://en.wikipedia.org/wiki/Mixture_model
 * http://en.wikipedia.org/wiki/Normal_distribution#Moments
 * http://stats.stackexchange.com/questions/16608/what-is-the-variance-of-the-weighted-mixture-of-two-gaussians
 *
 * @param z Mixture component of probabilities (prior)
 * @param x Conditional probabilities p(x|k) for all components of z (likelihood)
 */
case class MoG(z: Array[Double], x: Array[Gaussian]) {

  require(z.size == x.size, "Number of mixture components doesn't match the number of conditional gaussian probabilities.")

  /**
   * Returns Gaussian approximation of mixture of Gaussians by matching first and second moments.
   */
  def gaussianApprox(): Gaussian = {
    val Ex = z.zip(x).map { case (z, x) => z * x.m }.sum
    val Exx = z.zip(x).map { case (z, x) => z * (pow(x.m, 2) + x.v) }.sum
    Gaussian(Ex, Exx - pow(Ex, 2))
  }
}