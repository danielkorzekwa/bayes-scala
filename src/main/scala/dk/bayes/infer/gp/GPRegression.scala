package dk.bayes.infer.gp

import dk.bayes.math.linear._

trait GPRegression {

  /**
   * Gaussian Process Regression. It uses Gaussian likelihood and zero mean functions.
   *
   * @param z Inputs for making predictions. [NxD] matrix. N - number of test points, D - dimensionality of input space
   * @return Predicted targets.[mean variance]
   */
  def predict(z: Matrix): Matrix
}