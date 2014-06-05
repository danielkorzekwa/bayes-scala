package dk.bayes.infer.gp.mean

import dk.bayes.math.linear.Matrix

/**
 * Mean function
 */
trait MeanFunc {

  /**
   * Returns [N x 1] mean vector
   *
   * @param x [N x D] vector, N - number of random variables, D - dimensionality of random variable
   */
  def mean(x: Matrix): Matrix
}