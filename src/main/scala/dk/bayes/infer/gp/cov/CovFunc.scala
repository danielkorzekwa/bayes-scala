package dk.bayes.infer.gp.cov

import dk.bayes.math.linear._

/**
 * Covariance function that measures similarity between two points in some input space.
 *
 */
trait CovFunc {

  /**
   * @param x [N x D] vector, N - number of random variables, D - dimensionality of random variable
   * @return [N x N] covariance matrix
   */
  def cov(x: Matrix): Matrix =
    Matrix(x.numRows, x.numRows, (rowIndex: Int, colIndex: Int) => cov(x.row(rowIndex).t, x.row(colIndex).t))

  /**
   * @param x [N x D] vector, N - number of random variables, D - dimensionality of random variable
   * @param z [M x D] vector, N - number of random variables, D - dimensionality of random variable
   * @return [N x M] covariance matrix
   */
  def covNM(x: Matrix, z: Matrix): Matrix =
    Matrix(x.numRows, z.numRows, (rowIndex: Int, colIndex: Int) => cov(x.row(rowIndex).t, z.row(colIndex).t))

  /**
   * Returns similarity between two vectors.
   *
   * @param x1 [Dx1] vector
   * @param x2 [Dx1] vector
   */
  def cov(x1: Matrix, x2: Matrix): Double

  def cov(x1: Double, x2: Double): Double = cov(Matrix(x1), Matrix(x2))
}