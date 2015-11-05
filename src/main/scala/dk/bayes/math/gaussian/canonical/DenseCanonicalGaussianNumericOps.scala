package dk.bayes.math.gaussian.canonical

import dk.bayes.math.numericops._

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
      }
    }

    def apply(a: DenseCanonicalGaussian, b: DenseCanonicalGaussian): DenseCanonicalGaussian = {
      if (b.g.isNaN) a else DenseCanonicalGaussianOps.*(a, b)
    }
  }

  /**
   * Returns quotient of two canonical gausssians
   */
  implicit val divideOp = new divideOp[DenseCanonicalGaussian] {
    def apply(a: DenseCanonicalGaussian, b: DenseCanonicalGaussian): DenseCanonicalGaussian = {
      if (b.g.isNaN()) a else DenseCanonicalGaussianOps./(a, b)
    }
  }

  implicit val isIdentical = new isIdentical[DenseCanonicalGaussian] {
    def apply(x1: DenseCanonicalGaussian, x2: DenseCanonicalGaussian, tolerance: Double): Boolean = {

      val isMeanIdentical = dk.bayes.math.linear.isIdentical(x1.mean, x2.mean, tolerance)
      val isVarIdentical = dk.bayes.math.linear.isIdentical(x1.variance, x2.variance, tolerance)
      isMeanIdentical && isVarIdentical
    }
  }
}