package dk.bayes.math.gaussian

import scala.math._

/**
 * Kalman Formulas following chapter 15 from the book 'Stuart Russell, Peter Norvig. Artificial Intelligence - A Modern Approach, Third Edition, 2010'
 *
 * @author Daniel Korzekwa
 */
object KalmanFilter {

  /**
   * Returns marginal for x1: P(x1) = N(m_x1,v_x1)
   *
   * @param x0 Location at the time t0:  P(x0) = N(m,v)
   * @param x1Var Variance for conditional location at the time t1: P(x1|x0) ~ N(A*x0,x1Var)
   */
  def marginal(x0: Gaussian, A: Double, x1Var: Double): Gaussian = Gaussian(A * x0.m, x1Var + A * x0.v * A)

  /**
   * Returns marginal for x1: P(x1) = N(m_x1,v_x1)
   *
   * @param x0 Location at the time t0:  P(x0) = N(m,v)
   * @param x1Var Variance for conditional location at the time t1: P(x1|x0) N(x0,x1Var)
   */
  def marginal(x0: Gaussian, x1Var: Double): Gaussian = Gaussian(x0.m, x0.v + x1Var)

  /**
   * Returns posterior for x given the observed value of z: P(x|z) = N(m,v)
   *
   *  @param x P(x) = N(m,v)
   *  @param zVar Variance for conditional observation variable: P(z|x) N(x,zV)
   *  @param evidence Observed value for z
   *
   */
  def posterior(x: Gaussian, zVar: Double, evidence: Double): Gaussian = {
    val m = (x.v * evidence + zVar * x.m) / (x.v + zVar)
    val v = x.v * zVar / (x.v + zVar)
    Gaussian(m, v)
  }

  /**
   * Returns posterior for x given the observed value of z: P(x|z) = N(m,v)
   *
   *  @param x P(x) = N(m,v)
   *  @param A P(z|x) = N(A*x,zVar)
   *  @param zVar Variance for conditional observation variable: P(z|x) = N(A*x,zVar)
   *  @param evidence Observed value for z
   *
   */
  def posterior(x: Gaussian, A: Double, zVar: Double, evidence: Double): Gaussian = {
    val v = 1d / (1d / x.v + A * (1d / zVar) * A)
    val m = v * (A * (1d / zVar) * evidence + (1d / x.v) * x.m)
    Gaussian(m, v)
  }

  /**
   * Returns posterior for x given the observed value of z: P(x|z) = N(m,v)
   *
   *  @param x P(x) = N(m,v)
   *  @param zVar Variance for conditional observation variable: P(z|x) N(x,zV)
   *  @param evidence Observed values for z
   *
   */
  def posterior(x: Gaussian, zVar: Double, evidence: Seq[Double]): Gaussian = {
    evidence.foldLeft(x)((mean, value) => KalmanFilter.posterior(mean, zVar, value))
  }

}