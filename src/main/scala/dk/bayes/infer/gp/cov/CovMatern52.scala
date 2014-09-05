package dk.bayes.infer.gp.cov

import dk.bayes.math.linear.Matrix
import scala.math._

/**
 * Matern covariance with v=5/2
 *
 * Implementation based 'http://www.gaussianprocess.org/gpml/code/matlab/doc/index.html'
 *
 * @param sf - log of signal standard deviation
 * @param ell - log of length scale standard deviation
 */
case class CovMatern52(sf: Double, ell: Double) extends CovFunc {

  def cov(x1: Matrix, x2: Matrix): Double = {

    val P = calcP(x1.size)
    // r is the distance sqrt((x^p-x^q)'*inv(P)*(x^p-x^q))
    val r = calcR(x1, x2, P)
    val t1 = calcT1(r)
    val t2 = calcT2(r)

    exp(2 * sf) * ((1 + t1 + t2) * exp(-t1))
  }

  /**
   * Returns derivative of similarity between two vectors with respect to sf.
   *
   * @param x1 [Dx1] vector
   * @param x2 [Dx1] vector
   */
  def df_dSf(x1: Matrix, x2: Matrix): Double = {
    require(x1.size == x2.size, "Vectors x1 and x2 have different sizes")

    2 * cov(x1, x2)
  }

  /**
   * Returns derivative of similarity between two vectors with respect to ell.
   *
   * @param x1 [Dx1] vector
   * @param x2 [Dx1] vector
   */
  def df_dEll(x1: Matrix, x2: Matrix): Double = {
    require(x1.size == x2.size, "Vectors x1 and x2 have different sizes")

    val P = calcP(x1.size)

    // r is the distance sqrt((x^p-x^q)'*inv(P)*(x^p-x^q))
    val r = calcR(x1, x2, P)

    if (r == 0) 0 else {

      val t1 = calcT1(r)
      val t2 = calcT2(r)

      //derivative of Pinv with respect to ell 
      val elemWiseD = 2 * exp(2 * ell) * Matrix.identity(x1.size)

      //derivative of r^2 with respect to ell
      val dr2_dell = ((x1 - x2).t * (-1 * P * elemWiseD * P) * (x1 - x2))(0)

      val term1 = ((1 + t1 + t2) * exp(-t1)) * (-sqrt(5)) * (0.5 / r) * dr2_dell
      val term2 = exp(-t1) * (sqrt(5) * (0.5 / r) * dr2_dell + (5d / 3) * dr2_dell)

      exp(2 * sf) * (term1 + term2)
    }
  }

  private def calcR(x1: Matrix, x2: Matrix, P: Matrix): Double = {
    sqrt(((x1 - x2).t * P * (x1 - x2))(0))
  }
  private def calcT1(r: Double): Double = (sqrt(5) * r)
  private def calcT2(r: Double): Double = (5d / 3) * (r * r)
  private def calcP(size: Int): Matrix = (exp(2 * ell) * Matrix.identity(size)).inv
}