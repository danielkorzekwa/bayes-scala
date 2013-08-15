package dk.bayes.math.gaussian

import Linear._

/**
 *  Multiples two canonical gaussians.
 *
 * @author Daniel Korzekwa
 */
object CanonicalGaussianMultiply {

  def *(gaussian1: CanonicalGaussian, gaussian2: CanonicalGaussian): CanonicalGaussian = {

    val commonVariableIds = gaussian1.varIds.union(gaussian2.varIds).distinct

    *(commonVariableIds, gaussian1, gaussian2)
  }

  def *(commonVariableIds: Array[Int], gaussian1: CanonicalGaussian, gaussian2: CanonicalGaussian): CanonicalGaussian = {

    val newK = extendedScopeK(commonVariableIds, gaussian1.varIds, gaussian1.k) + extendedScopeK(commonVariableIds, gaussian2.varIds, gaussian2.k)
    val newH = extendedScopeH(commonVariableIds, gaussian1.varIds, gaussian1.h) + extendedScopeH(commonVariableIds, gaussian2.varIds, gaussian2.h)
    val newG = gaussian1.g + gaussian2.g

    CanonicalGaussian(commonVariableIds, newK, newH, newG)
  }

  private def extendedScopeK(newVarIds: Array[Int], kVarIds: Array[Int], k: Matrix): Matrix = {

    val extendedK = if (newVarIds.sameElements(kVarIds)) k else {
      val kMatrix = Matrix(newVarIds.size, newVarIds.size)

      k.foreach { (rowId, colId) =>
        val cellValue = k(rowId, colId)

        val newRowId = newVarIds.indexOf(kVarIds(rowId))
        val newColId = newVarIds.indexOf(kVarIds(colId))

        kMatrix.set(newRowId, newColId, cellValue)
      }
      kMatrix
    }

    extendedK
  }

  private def extendedScopeH(newVarIds: Array[Int], hVarIds: Array[Int], h: Matrix): Matrix = {

    val extendedH = if (newVarIds.sameElements(hVarIds)) h else {
      val hMatrix = Matrix(newVarIds.size, 1)

      h.foreach { (rowId, colId) =>
        val cellValue = h(rowId, colId)
        val newRowId = newVarIds.indexOf(hVarIds(rowId))

        hMatrix.set(newRowId, 0, cellValue)
      }
      hMatrix
    }
    extendedH
  }
}