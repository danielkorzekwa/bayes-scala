package dk.bayes.gaussian

/**
 * Kalman Formulas following chapter 15 from the book 'Stuart Russell, Peter Norvig. Artificial Intelligence - A Modern Approach, Third Edition, 2010'
 *
 * @author Daniel Korzekwa
 */
object KalmanFilter {

  /**
   * Returns marginal for x1: P(x1) = N(mu,sigma)
   *
   * @param x0 Location at the time t0:  P(x0) = N(mu,sigma)
   * @param x1Sigma Variance for conditional location at the time t1: P(x1|x0) N(x0,x1Sigma)
   */
  def marginal(x0: Gaussian, x1Sigma: Double): Gaussian = Gaussian(x0.mu,x0.sigma + x1Sigma)

  /**
   * Returns posterior for x given the observed value of z: P(x|z) = N(mu,sigma)
   *
   *  @param x P(x) = N(mu,sigma)
   *  @param zSigma Variance for conditional observation variable: P(z|x) N(x,zSigma)
   *  @param evidence Observed value for z
   *
   */
  def posterior(x: Gaussian, zSigma: Double, evidence: Double): Gaussian = {
    val mu = (x.sigma * evidence + zSigma * x.mu) / (x.sigma + zSigma)
    val sigma = x.sigma * zSigma / (x.sigma + zSigma)
    Gaussian(mu, sigma)
  }
}