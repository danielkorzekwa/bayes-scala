package dk.bayes.infer.gp.cov

import scala.math._

import breeze.linalg.DenseMatrix
import dk.bayes.math.linear._

/**
 * Implementation based 'http://www.gaussianprocess.org/gpml/code/matlab/doc/index.html'
 *
 *  Squared Exponential covariance function with isotropic distance measure. The
 * covariance function is parameterized as:
 *
 * k(x^p,x^q) = sf^2 * exp(-(x^p - x^q)'*inv(P)*(x^p - x^q)/2)
 *
 * where the P matrix is ell^2 times the unit matrix and sf^2 is the signal
 * variance.
 *
 * Copyright (c) by Carl Edward Rasmussen and Hannes Nickisch, 2010-09-10.
 *
 * @param sf - log of signal standard deviation
 * @param ell - log of length scale standard deviation
 */

case class CovSEiso(sf: Double, ell: Double) extends CovFunc {

  def covD(x: DenseMatrix[Double]): Array[DenseMatrix[Double]] = {
    val covDfDSf = df_dSf(x)
    val covDfDEll = df_dEll(x)

    Array(covDfDSf, covDfDEll)
  }

  def covNMd(x: DenseMatrix[Double], z: DenseMatrix[Double]): Array[DenseMatrix[Double]] = {
    val covDfDSf = df_dSf(x, z)
    val covDfDEll = df_dEll(x, z)

    Array(covDfDSf, covDfDEll)
  }

  def cov(x1: Array[Double], x2: Array[Double]): Double = {
    require(x1.size == x2.size, "Vectors x1 and x2 have different sizes")
    val expArg = -0.5 * distance(x1, x2, exp(2 * ell))
    exp(2 * sf) * exp(expArg)
  }

  /**
   * Returns covariance matrix of element wise partial derivatives with respect to sf
   *
   * @param x [N x D] vector, N - number of random variables, D - dimensionality of random variable
   *
   */
  def df_dSf(x: DenseMatrix[Double]): DenseMatrix[Double] =
    DenseMatrix.tabulate(x.rows, x.rows) { case (rowIndex: Int, colIndex: Int) => df_dSf(x(rowIndex, ::).t.toArray, x(colIndex, ::).t.toArray) }
  def df_dSf(x: DenseMatrix[Double], z: DenseMatrix[Double]): DenseMatrix[Double] =
    DenseMatrix.tabulate(x.rows, z.rows) { case (rowIndex: Int, colIndex: Int) => df_dSf(x(rowIndex, ::).t.toArray, z(colIndex, ::).t.toArray) }
  def df_dSf(x: Array[Double]): DenseMatrix[Double] =
    DenseMatrix.tabulate(x.size, x.size) { case (rowIndex: Int, colIndex: Int) => df_dSf(x(rowIndex), x(colIndex)) }

  /**
   * Returns derivative of similarity between two vectors with respect to sf.
   *
   * @param x1 [Dx1] vector
   * @param x2 [Dx1] vector
   */
  def df_dSf(x1: Array[Double], x2: Array[Double]): Double = {
    require(x1.size == x2.size, "Vectors x1 and x2 have different sizes")

    val expArg = -0.5 * distance(x1, x2, exp(2 * ell))
    2 * exp(2 * sf) * exp(expArg)
  }
  def df_dSf(x1: Double, x2: Double): Double = df_dSf(Array(x1), Array(x2))
  /**
   * Returns covariance matrix of element wise partial derivatives with respect to ell
   *
   * @param x [N x D] vector, N - number of random variables, D - dimensionality of random variable
   *
   */
  def df_dEll(x: DenseMatrix[Double]): DenseMatrix[Double] =
    DenseMatrix.tabulate(x.rows, x.rows) { case (rowIndex: Int, colIndex: Int) => df_dEll(x(rowIndex, ::).t.toArray, x(colIndex, ::).t.toArray) }

  def df_dEll(x: DenseMatrix[Double], z: DenseMatrix[Double]): DenseMatrix[Double] =
    DenseMatrix.tabulate(x.rows, z.rows) { case (rowIndex: Int, colIndex: Int) => df_dEll(x(rowIndex, ::).t.toArray, z(colIndex, ::).t.toArray) }
  def df_dEll(x: Array[Double]): DenseMatrix[Double] =
    DenseMatrix.tabulate(x.size, x.size) { case (rowIndex: Int, colIndex: Int) => df_dEll(x(rowIndex), x(colIndex)) }

  /**
   * Returns derivative of similarity between two vectors with respect to ell.
   *
   * @param x1 [Dx1] vector
   * @param x2 [Dx1] vector
   */
  def df_dEll(x1: Array[Double], x2: Array[Double]): Double = {
    require(x1.size == x2.size, "Vectors x1 and x2 have different sizes")

    val dfDEll = if (x1.size == 1 && x2.size == 1 && x1(0) == x2(0)) 0
    else {
      val expArg = -0.5 * distance(x1, x2, exp(2 * ell))
      val d = -0.5 * distance(x1, x2, exp(2 * ell) / (-2d))

      exp(2 * sf) * exp(expArg) * d
    }
    dfDEll
  }

  def df_dEll(x1: Double, x2: Double): Double = {
    df_dEll(Array(x1), Array(x2))
  }

  private def distance(x1: Array[Double], x2: Array[Double], l: Double): Double = {

    var distance = 0d
    var i = 0

    while (i < x1.size) {
      distance += pow(x1(i) - x2(i), 2) / l
      i += 1
    }

    distance
  }

}