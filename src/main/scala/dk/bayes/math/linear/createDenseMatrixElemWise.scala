package dk.bayes.math.linear

import breeze.linalg.DenseMatrix

object createDenseMatrixElemWise {

  def apply(numRows: Int, numCols: Int, cell: (Int, Int) => Double): DenseMatrix[Double] = {

    val data = DenseMatrix.zeros[Double](numRows, numCols)

    for (rowIndex <- 0 until numRows) {

      for (colIndex <- 0 until numCols) {

        data(rowIndex, colIndex) = cell(rowIndex, colIndex)
      }

    }

    data

  }
}