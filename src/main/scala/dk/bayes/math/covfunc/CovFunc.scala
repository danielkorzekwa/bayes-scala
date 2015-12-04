package dk.bayes.math.covfunc

import dk.bayes.math.linear._
import breeze.linalg.DenseMatrix

/**
 * Covariance function that measures similarity between two points in some input space.
 *
 */
trait CovFunc {

  /**
   * @param x [N x D] vector, N - number of random variables, D - dimensionality of random variable
   * @return [N x N] covariance matrix
   */
  def cov(x: DenseMatrix[Double]): DenseMatrix[Double] =
    DenseMatrix.tabulate(x.rows, x.rows) { case (rowIndex, colIndex) => cov(x(rowIndex, ::).t.toArray, x(colIndex, ::).t.toArray) }

  def cov(x: Array[Double]): DenseMatrix[Double] =
    DenseMatrix.tabulate(x.size, x.size) { case (rowIndex, colIndex) => cov(x(rowIndex), x(colIndex)) }

  /**
   * @param x [N x D] vector, N - number of random variables, D - dimensionality of random variable
   * @param z [M x D] vector, N - number of random variables, D - dimensionality of random variable
   * @return [N x M] covariance matrix
   */
  def covNM(x: DenseMatrix[Double], z: DenseMatrix[Double]): DenseMatrix[Double] =
    DenseMatrix.tabulate(x.rows, z.rows) { case (rowIndex: Int, colIndex: Int) => cov(x(rowIndex, ::).t.toArray, z(colIndex, ::).t.toArray) }

  /**
   * Returns similarity between two vectors.
   *
   * @param x1 [Dx1] vector
   * @param x2 [Dx1] vector
   */
  def cov(x1: Array[Double], x2: Array[Double]): Double

  def cov(x1: Double, x2: Double): Double = cov(Array(x1), Array(x2))
}