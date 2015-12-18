package dk.bayes.math.gaussian.canonical

import dk.bayes.math.numericops._
import breeze.linalg.diag

/**
 * @author Daniel Korzekwa
 */
trait DenseCanonicalGaussianNumericOps {

  /**
   * Returns product of multiplying two canonical gausssians
   */

  implicit val multOp = new multOp[DenseCanonicalGaussian] {

    def apply(a: DenseCanonicalGaussian*): DenseCanonicalGaussian = {
      a match {
        case Seq(a, b) => apply(a, b)
        case _         => a.reduceLeft((total, b) => DenseCanonicalGaussianOps.*(total, b))
      }
    }

    def apply(a: DenseCanonicalGaussian, b: DenseCanonicalGaussian): DenseCanonicalGaussian = {
      DenseCanonicalGaussianOps.*(a, b)
    }
  }

  /**
   * Returns quotient of two canonical gausssians
   */
  implicit val divideOp = new divideOp[DenseCanonicalGaussian] {
    def apply(a: DenseCanonicalGaussian, b: DenseCanonicalGaussian): DenseCanonicalGaussian = {
      DenseCanonicalGaussianOps./(a, b)
    }
  }

  implicit val isIdentical = new isIdentical[DenseCanonicalGaussian] {
    def apply(x1: DenseCanonicalGaussian, x2: DenseCanonicalGaussian, tolerance: Double): Boolean = {

      val isKIdentical = dk.bayes.math.linear.isIdentical(diag(x1.k), diag(x2.k), tolerance)
      val isHIdentical = dk.bayes.math.linear.isIdentical(x1.h, x2.h, tolerance)
      isKIdentical && isHIdentical

    }
  }
}