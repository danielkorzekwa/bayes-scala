package dk.bayes.math.gaussian

import dk.bayes.math.numericops._

/**
 * @author Daniel Korzekwa
 */
trait CanonicalGaussianNumericOps {

   /**
   * Returns product of multiplying two canonical gausssians
   */
  implicit val multOp = new multOp[CanonicalGaussian, CanonicalGaussian] {
    def apply(a: CanonicalGaussian, b: CanonicalGaussian): CanonicalGaussian = {
      if (b.g.isNaN) a else CanonicalGaussianOps.*(a, b)
    }
  }

  /**
   * Returns quotient of two canonical gausssians
   */
  implicit val divideOp = new divideOp[CanonicalGaussian, CanonicalGaussian] {
    def apply(a: CanonicalGaussian, b: CanonicalGaussian): CanonicalGaussian = {
      if (b.g.isNaN()) a else CanonicalGaussianOps./(a, b)
    }
  }

  implicit val isIdentical = new isIdentical[CanonicalGaussian, CanonicalGaussian] {
    def apply(x1: CanonicalGaussian, x2: CanonicalGaussian, tolerance: Double): Boolean = {
      x1.mean.isIdentical(x2.mean, tolerance) &&
        x1.variance.isIdentical(x2.variance, tolerance)
    }
  }
}