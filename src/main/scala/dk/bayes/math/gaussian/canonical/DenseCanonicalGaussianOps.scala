package dk.bayes.math.gaussian.canonical

import dk.bayes.math.linear._
import breeze.linalg.DenseVector
import breeze.linalg.DenseMatrix

/**
 *  Multiples two canonical gaussians.
 *
 * @author Daniel Korzekwa
 */
object DenseCanonicalGaussianOps {

  def *(gaussian1: DenseCanonicalGaussian, gaussian2: DenseCanonicalGaussian): DenseCanonicalGaussian = {

    val newK = gaussian1.k + gaussian2.k
    val newH = gaussian1.h + gaussian2.h
    val newG = gaussian1.g + gaussian2.g

    DenseCanonicalGaussian(newK, newH, newG)
  }

  def /(gaussian1: DenseCanonicalGaussian, gaussian2: DenseCanonicalGaussian): DenseCanonicalGaussian = {

    val newK = gaussian1.k - gaussian2.k
    val newH = gaussian1.h - gaussian2.h
    val newG = gaussian1.g - gaussian2.g

    DenseCanonicalGaussian(newK, newH, newG)
  }

  /**
   * Extends the scope of Gaussian.
   * It is useful for * and / operations on Gaussians with different variables.
   *
   * @param gaussian Gaussian to extend
   * @param size The size of extended Gaussian
   * @param startIndex The position of this Gaussian in the new extended Gaussian
   */
  def extend(gaussian: DenseCanonicalGaussian, size: Int, startIndex: Int): DenseCanonicalGaussian = {

    val newK = extendedScopeK(size, startIndex, gaussian.k)
    val newH = extendedScopeH(size, startIndex, gaussian.h)

    gaussian.copy(k = newK, h = newH)
  }

  private def extendedScopeK(size: Int, startIndex: Int, k: DenseMatrix[Double]): DenseMatrix[Double] = {

    val extendedK = if (startIndex == 0) {
      val kMatrix = DenseMatrix.zeros[Double](size, size)
      kMatrix(0 until k.rows,0 until k.cols) := k
      kMatrix
    } else {
      val kMatrix = DenseMatrix.zeros[Double](size, size)

      k.foreachKey{case (rowId,colId) => 
      
         val cellValue = k(rowId, colId)

        val newRowId = startIndex + rowId
        val newColId = startIndex + colId

        kMatrix(newRowId, newColId) = cellValue
      }
      
      kMatrix
    }

    extendedK
  }

  private def extendedScopeH(size: Int, startIndex: Int, h: DenseVector[Double]): DenseVector[Double] = {

    val extendedH = if (startIndex == 0) {
      val hMatrix = DenseVector.zeros[Double](size)
      
      hMatrix(0 until h.size) := h
      hMatrix
    } else {
      val hMatrix = DenseVector.zeros[Double](size)

      h.foreachKey { rowId => 
        
        val cellValue = h(rowId)
        val newRowId = startIndex + rowId

        hMatrix(newRowId) = cellValue
        
        }
      
      hMatrix
    }
    extendedH
  }
}