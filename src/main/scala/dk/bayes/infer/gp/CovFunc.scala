package dk.bayes.infer.gp

import dk.bayes.math.linear._

/**
 * Covariance function that measures similarity between two points in some input space.
 *
 */
trait CovFunc {

  /**
   * @param v [N x D] vector, N - number of random variables, D - dimensionality of random variable
   * @return [N x N] covariance matrix
   */
  def cov(x: Matrix): Matrix =
    Matrix(x.numRows, x.numRows, (rowIndex: Int, colIndex: Int) => cov(x.row(rowIndex).t, x.row(colIndex).t))

  /**
   * Returns similarity between two vectors.
   *
   * @param x1 [Dx1] vector
   * @param x2 [Dx1] vector
   */
  def cov(x1: Matrix, x2: Matrix): Double
}