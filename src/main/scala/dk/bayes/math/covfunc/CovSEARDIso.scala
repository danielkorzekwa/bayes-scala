package dk.bayes.math.covfunc

import scala.math._
import breeze.linalg.DenseMatrix
import breeze.linalg._

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
 * @param ell - vector of log of length scale standard deviation
 */

case class CovSEARDIso(sf: Double, ell: Array[Double]) extends CovFunc {

  private val l = ell.map(x => exp(2 * x))

  /**
   * @param x [N x D] vector, N - number of random variables, D - dimensionality of random variable
   * @return ([N x N] covariance matrix, [N x N] partial derivatives matrix with respect to sf parameter, array of [NxN] partial derivative matrix with respect to ell parameters)
   */
  def covWithD(x: DenseMatrix[Double], computeDfDSf: Boolean = true, computeDfDell: Boolean = true): Tuple3[DenseMatrix[Double], Option[DenseMatrix[Double]], Option[Array[DenseMatrix[Double]]]] = {
    val covariance = cov(x)
   
    val covDfSF = if (computeDfDSf) Some(2d * covariance) else None

    val covDfDell = if (computeDfDell) {

      val distanceDMatrixArray = (0 until ell.size).map { i => distanceMatrixD(x(::,i).toArray, l(i)) }

      val covDfDell = distanceDMatrixArray.map(m => covariance.:*(-0.5 * m)).toArray
      Some(covDfDell)
    } else None

    (covariance, covDfSF, covDfDell)
  }

  def cov(x1: DenseVector[Double], x2: DenseVector[Double]): Double = {
    cov(x1.toArray, x2.toArray)
  }

  def cov(x1: Array[Double], x2: Array[Double]): Double = {
    require(x1.size == x2.size, "Vectors x1 and x2 have different sizes")

    val expArg = -0.5 * distance(x1, x2)
    exp(2 * sf) * exp(expArg)
  }

  private def distance(x1: Array[Double], x2: Array[Double]): Double = {

    var distance = 0d
    var i = 0
    while (i < x1.size) {
      val x1Val = x1(i)
      val x2Val = x2(i)
      if (x1Val != x2Val) {
        val d = x1Val - x2Val
        distance += (d * d) / l(i)
      }
      i += 1
    }

    distance
  }

  private def distanceMatrixD(x: Array[Double], l: Double) = {
   DenseMatrix.tabulate(x.size,x.size){case (rowIndex: Int, colIndex: Int) => distanceD(x(rowIndex), x(colIndex), l)}
  }

  private def distanceD(x1: Double, x2: Double, l: Double): Double = {

    if (x1 == x2) return 0
    else {
      val d = x1 - x2
      (d * d) / (l / (-2d))
    }
  }

}