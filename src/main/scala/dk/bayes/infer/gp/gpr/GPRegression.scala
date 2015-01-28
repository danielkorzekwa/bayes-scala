package dk.bayes.infer.gp.gpr

import dk.bayes.math.linear._

trait GPRegression {

  /**
   * Gaussian Process Regression. It uses Gaussian likelihood and zero mean functions.
   *
   * @param z Inputs for making predictions. [NxD] matrix. N - number of test points, D - dimensionality of input space
   * @return Predicted targets.[mean variance]
   */
  def predict(z: Matrix): Matrix

  /**
   * Returns marginal likelihood int p(f|x)*p(y|f,x)df
   */
  def loglik(): Double

  /**
   * Returns partial derivative of log marginal likelihood int p(f|x)*p(y|f,x)df .
   *
   * @param covElemWiseD Element wise partial derivatives of covariance matrix K with respect to some parameter. K is given by: p(y) ~ N(mu,K)
   */
  def loglikD(covElemWiseD: Matrix): Double
}