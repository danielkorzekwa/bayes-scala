package dk.bayes.math.gaussian

import scala.math._
import dk.bayes.math.numericops._

trait GaussianNumericOps {

  private val minPrecision = 1e-7

  /**
   * P.A. Bromiley. Products and Convolutions of Gaussian Distributions, 2003
   */
  implicit val multOp = new multOp[Gaussian] {

    def apply(a: Gaussian*): Gaussian = {

      a match {
        case Seq(a, b) => apply(a, b)
        case a         => a.reduceLeft((total, b) => apply(total, b))
      }

    }

    def apply(a: Gaussian, b: Gaussian): Gaussian = {

      val product =
        if (b.v == Double.PositiveInfinity) a
        else if (a.v == Double.PositiveInfinity) b
        else {
          val newM = (a.m * b.v + b.m * a.v) / (a.v + b.v)
          val newV = (a.v * b.v) / (a.v + b.v)
          Gaussian(newM, newV)
        }

      product
    }
  }

  /**
   * Thomas Minka. EP: A quick reference, 2008
   */
  implicit val divideOp = new divideOp[Gaussian] {
    def apply(a: Gaussian, b: Gaussian): Gaussian = {
      if (a.v == Double.PositiveInfinity || b.v == Double.PositiveInfinity) a
      else {
        val newPrecision = (1 / a.v - 1 / b.v)
        val newV = if (abs(newPrecision) > minPrecision) 1 / newPrecision else Double.PositiveInfinity
        val newM = if (newV.isPosInfinity) 0 else newV * (a.m / a.v - b.m / b.v)
        Gaussian(newM, newV)
      }
    }
  }

  implicit val isIdentical = new isIdentical[Gaussian] {
    def apply(x1: Gaussian, x2: Gaussian, tolerance: Double): Boolean = {
      val theSame = abs(x1.m - x2.m) < tolerance &&
        abs(x1.v - x2.v) < tolerance &&
        x1.v > 0 && x2.v > 0
        
        theSame
    }
  }

}