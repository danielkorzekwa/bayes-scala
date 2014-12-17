package dk.bayes.infer.gp.cov

import dk.bayes.math.linear._
import scala.math._

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

  def cov(x1: Matrix, x2: Matrix): Double = {
    cov(x1.toArray, x2.toArray)
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
  def df_dSf(x: Matrix): Matrix =
    Matrix(x.numRows, x.numRows, (rowIndex: Int, colIndex: Int) => df_dSf(x.row(rowIndex).t, x.row(colIndex).t))

  /**
   * Returns derivative of similarity between two vectors with respect to sf.
   *
   * @param x1 [Dx1] vector
   * @param x2 [Dx1] vector
   */
  def df_dSf(x1: Matrix, x2: Matrix): Double = {
    require(x1.size == x2.size, "Vectors x1 and x2 have different sizes")

    val expArg = -0.5 * distance(x1.toArray, x2.toArray, exp(2 * ell))
    2 * exp(2 * sf) * exp(expArg)
  }

  /**
   * Returns covariance matrix of element wise partial derivatives with respect to ell
   *
   * @param x [N x D] vector, N - number of random variables, D - dimensionality of random variable
   *
   */
  def df_dEll(x: Matrix): Matrix =
    Matrix(x.numRows, x.numRows, (rowIndex: Int, colIndex: Int) => df_dEll(x.row(rowIndex).t, x.row(colIndex).t))

  /**
   * Returns derivative of similarity between two vectors with respect to ell.
   *
   * @param x1 [Dx1] vector
   * @param x2 [Dx1] vector
   */
  def df_dEll(x1: Matrix, x2: Matrix): Double = {
    require(x1.size == x2.size, "Vectors x1 and x2 have different sizes")

    val expArg = -0.5 * distance(x1.toArray, x2.toArray, exp(2 * ell))
    val d = -0.5 * distance(x1.toArray, x2.toArray, exp(2 * ell) / (-2d))

    exp(2 * sf) * exp(expArg) * d
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