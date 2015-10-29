package dk.bayes.infer.gp.mean

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector

/**
 * Mean function
 */
trait MeanFunc {

  /**
   * Returns [N x 1] mean vector
   *
   * @param x [N x D] vector, N - number of random variables, D - dimensionality of random variable
   */
  def mean(x:  DenseMatrix[Double]):  DenseVector[Double]
}