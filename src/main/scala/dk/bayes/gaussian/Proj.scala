package dk.bayes.gaussian

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
   * @param dZ Derivative of Z with respect to q_mu
   */
  def projMu(q: Gaussian, Z: Double, dZ: Double) = q.mu + q.sigma * (dZ / Z)

  /**
   * Equation 3.19 (page 15) from Minka Thesis
   *
   * @param q Function q(x)
   * @param Z Normalisation constant for distribution Z(m,v)=f(x)*q(x)
   * @param dZ_mu Derivative of Z with respect to q_mu
   * @param dZ_sigma Derivative of Z with respect to q_sigma
   */
  def projSigma(q: Gaussian, Z: Double, dZ_mu: Double, dZ_sigma: Double): Double = {
    val logZ_mu = dZ_mu / Z
    val logZ_sigma = dZ_sigma / Z
    q.sigma - q.sigma * q.sigma * (logZ_mu * logZ_mu - 2 * logZ_sigma)
  }
}