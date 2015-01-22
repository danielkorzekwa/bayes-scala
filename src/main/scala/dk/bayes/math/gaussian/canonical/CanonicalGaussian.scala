package dk.bayes.math.gaussian.canonical

import dk.bayes.math.numericops._
import dk.bayes.math.linear.Matrix

trait CanonicalGaussian {

}

object CanonicalGaussian {

  /**
   * Returns product of multiplying two canonical gausssians
   */
  implicit val multOp = new multOp[CanonicalGaussian, CanonicalGaussian] {
    def apply(a: CanonicalGaussian, b: CanonicalGaussian): CanonicalGaussian = {

      val result = a match {
        case a: DenseCanonicalGaussian => {
          b match {
            case b: DenseCanonicalGaussian  => a * b
            case b: SparseCanonicalGaussian => denseMultSparse(a, b)
          }
        }
      }

      result
    }
  }

  private def denseMultSparse(a: DenseCanonicalGaussian, b: SparseCanonicalGaussian): DenseCanonicalGaussian = {

    val newK = a.k + Matrix(a.h.size, a.h.size, b.k.toDenseMatrix.data)
    val newH = a.h + Matrix(b.h.toDenseVector.data)
    val newG = a.g + b.g
    new DenseCanonicalGaussian(newK, newH, newG)
  }

  /**
   * Returns quotient of two canonical gausssians
   */
  implicit val divideOp = new divideOp[CanonicalGaussian, CanonicalGaussian] {
    def apply(a: CanonicalGaussian, b: CanonicalGaussian): CanonicalGaussian = {

      throw new UnsupportedOperationException("Not implemented yet")
    }
  }

  implicit val isIdentical = new isIdentical[CanonicalGaussian, CanonicalGaussian] {
    def apply(x1: CanonicalGaussian, x2: CanonicalGaussian, tolerance: Double): Boolean = {
      val result = x1 match {
        case x1: DenseCanonicalGaussian => {
          x2 match {
            case x2: DenseCanonicalGaussian => {
              val isIdenticalDense = implicitly[isIdentical[DenseCanonicalGaussian, DenseCanonicalGaussian]]
              isIdenticalDense(x1, x2, tolerance)
            }
          }
        }
      }
      result
    }
  }
}