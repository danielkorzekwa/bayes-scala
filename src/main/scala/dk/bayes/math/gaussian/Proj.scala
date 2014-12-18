package dk.bayes.math.gaussian

import scala.math._

/**
 * Projects f(x)*q(x) distribution to a gaussian distribution q_new(x) by matching mean and variance moments.
 *
 *  Thomas P Minka. A family of algorithms for approximate Bayesian inference, 2001
 *
 *  @author Daniel Korzekwa
 */
object Proj {

  /**
   * Equation 3.18 (page 15) from Minka Thesis
   *
   * @param q Function q(x)
   * @param Z Normalisation constant for distribution Z(m,v)=f(x)*q(x)
   * @param dZ Derivative of Z with respect to q_mean
   */
  def projMu(q: Gaussian, Z: Double, dZ_m: Double) = q.m + q.v * (dZ_m / Z)

  /**
   * Equation 3.19 (page 15) from Minka Thesis
   *
   * @param q Function q(x)
   * @param Z Normalisation constant for distribution Z(m,v)=f(x)*q(x)
   * @param dZ_m Derivative of Z with respect to q_mean
   * @param dZ_v Derivative of Z with respect to q_variance
   */
  def projSigma(q: Gaussian, Z: Double, dZ_m: Double, dZ_v: Double): Double = {
    val logZ_m = dZ_m / Z
    val logZ_v = dZ_v / Z
    q.v - q.v * q.v * (logZ_m * logZ_m - 2 * logZ_v)
  }
}