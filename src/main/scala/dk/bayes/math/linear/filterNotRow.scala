package dk.bayes.math.linear

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector

object filterNotRow {

  def apply(x: DenseMatrix[Double], rowIndex: Int): DenseMatrix[Double] = {

    val newMatrix = rowIndex match {
      case 0                                    => x(1 until x.rows, ::)
      case rowIndex if rowIndex == (x.rows - 1) => x(0 until x.rows - 1, ::)
      case _ => {

        val top = x(0 until rowIndex, ::)
        val bottom = x(rowIndex + 1 until x.rows, ::)

        DenseMatrix.vertcat(top, bottom)
      }
    }
    newMatrix
  }
  
   def apply(x: DenseVector[Double], rowIndex: Int): DenseVector[Double] = {
    
     val newVector = rowIndex match {
      case 0                                    => x(1 until x.size)
      case rowIndex if rowIndex == (x.size - 1) => x(0 until x.size - 1)
      case _ => {

        val top = x(0 until rowIndex)
        val bottom = x(rowIndex + 1 until x.size)

        DenseVector.vertcat(top, bottom)
      }
    }
    newVector
   }
}