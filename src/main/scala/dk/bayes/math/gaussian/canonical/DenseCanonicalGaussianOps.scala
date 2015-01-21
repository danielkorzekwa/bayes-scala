package dk.bayes.math.gaussian.canonical

import dk.bayes.math.linear._

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

  private def extendedScopeK(size: Int, startIndex: Int, k: Matrix): Matrix = {

    val extendedK = if (startIndex == 0) {
      val kMatrix = Matrix.zeros(size, size)
      kMatrix.insertIntoThis(0, 0, k)
      kMatrix
    } else {
      val kMatrix = Matrix.zeros(size, size)

      k.foreach { (rowId, colId) =>
        val cellValue = k(rowId, colId)

        val newRowId = startIndex + rowId
        val newColId = startIndex + colId

        kMatrix.set(newRowId, newColId, cellValue)
      }
      kMatrix
    }

    extendedK
  }

  private def extendedScopeH(size: Int, startIndex: Int, h: Matrix): Matrix = {

    val extendedH = if (startIndex == 0) {
      val hMatrix = Matrix.zeros(size, 1)
      hMatrix.insertIntoThis(0, 0, h)
      hMatrix
    } else {
      val hMatrix = Matrix.zeros(size, 1)

      h.foreach { (rowId, colId) =>
        val cellValue = h(rowId, colId)
        val newRowId = startIndex + rowId

        hMatrix.set(newRowId, 0, cellValue)
      }
      hMatrix
    }
    extendedH
  }
}