package dk.bayes.infer.gp

import dk.bayes.math.linear._

trait GPRegression {

  /**
   * Gaussian Process Regression. It uses Gaussian likelihood and zero mean functions.
   *
   * @param x Inputs. [NxD] matrix, where N - number of training examples, D - dimensionality of input space
   * @param y Targets. [Nx1] matrix, where N - number of training examples
   * @param z Inputs for making predictions. [Nx1] matrix
   * @param covFunc Covariance function
   * @param noiseVar Noise variance of Gaussian likelihood function
   *
   * @return Predicted targets.[mean variance]
   */
  def predict(x: Matrix, y: Matrix, z: Matrix, covFunc: CovFunc, noiseVar: Double): Matrix
}