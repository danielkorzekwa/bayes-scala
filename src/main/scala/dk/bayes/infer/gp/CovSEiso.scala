package dk.bayes.infer.gp

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
 * @param sf - signal standard deviation
 * @param ell - length scale standard deviation
 */

case class CovSEiso(sf: Double, ell: Double) extends CovFunc {

  def cov(x1: Matrix, x2: Matrix): Double = {
    require(x1.size == x2.size, "Vectors x1 and x2 have different sizes")

    val Pinv = (ell * ell * Matrix.identity(x1.size)).inv

    val expArg = -((x1 - x2).t * Pinv * (x1 - x2))(0) / 2
    val covVal = sf * sf * exp(expArg)

    covVal
  }
}