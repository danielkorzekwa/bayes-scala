package dk.bayes.math.gaussian.canonical

import dk.bayes.math.numericops._
import dk.bayes.math.linear.Matrix

trait CanonicalGaussian {

}

object CanonicalGaussian {

  /**
   * Returns product of multiplying two canonical gausssians
   */
  implicit val multOp = new multOp[CanonicalGaussian] {

    def apply(a: CanonicalGaussian*): CanonicalGaussian = {

      a.head match {
        case head: SparseCanonicalGaussian => multiplySparse(a)
        case head: DenseCanonicalGaussian  => multiplyDense(a)
        case _                             => throw new UnsupportedOperationException("Not supported")
      }

    }

    def multiplySparse(a: Seq[CanonicalGaussian]): CanonicalGaussian = {

      val sparseA = a.map { a =>
        require(a.isInstanceOf[SparseCanonicalGaussian], "Not supported")
        a.asInstanceOf[SparseCanonicalGaussian]
      }

      val newK = sparseA.head.k.copy
      val newH = sparseA.head.h.copy
      var newG = sparseA.head.g

      sparseA.tail.foreach { a =>
        newK += a.k
        newH += a.h
        newG += a.g
      }
      val product = SparseCanonicalGaussian(newK, newH, newG)
      product
    }

    def multiplyDense(a: Seq[CanonicalGaussian]): CanonicalGaussian = {

      val denseA = a.map { a =>
        require(a.isInstanceOf[DenseCanonicalGaussian], "Not supported")
        a.asInstanceOf[DenseCanonicalGaussian]
      }

      val product = denseA.reduceLeft((total, b) => total * b)
      product
    }

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
  implicit val divideOp = new divideOp[CanonicalGaussian] {
    def apply(a: CanonicalGaussian, b: CanonicalGaussian): CanonicalGaussian = {

      val result = a match {
        case a: DenseCanonicalGaussian => {
          b match {
            case b: DenseCanonicalGaussian  => a / b
            case b: SparseCanonicalGaussian => denseDivideSparse(a, b)
          }
        }
      }

      result
    }
  }

  private def denseDivideSparse(a: DenseCanonicalGaussian, b: SparseCanonicalGaussian): DenseCanonicalGaussian = {

    val newK = a.k - Matrix(a.h.size, a.h.size, b.k.toDenseMatrix.data)
    val newH = a.h - Matrix(b.h.toDenseVector.data)
    val newG = a.g - b.g
    new DenseCanonicalGaussian(newK, newH, newG)
  }

  implicit val isIdentical = new isIdentical[CanonicalGaussian] {
    def apply(x1: CanonicalGaussian, x2: CanonicalGaussian, tolerance: Double): Boolean = {
      val result = x1 match {
        case x1: DenseCanonicalGaussian => {
          x2 match {
            case x2: DenseCanonicalGaussian => {
              val isIdenticalDense = implicitly[isIdentical[DenseCanonicalGaussian]]
              isIdenticalDense(x1, x2, tolerance)
            }
          }
        }
      }
      result
    }
  }
}