package dk.bayes.math.linear

import org.ejml.simple.SimpleMatrix
import org.ejml.ops.MatrixFeatures
import org.ejml.factory.CholeskyDecomposition
import org.ejml.data.DenseMatrix64F
import org.ejml.factory.DecompositionFactory

case class Matrix(matrix: SimpleMatrix) {

  def *(m: Matrix): Matrix = Matrix(this.matrix.mult(m.matrix))
  def +(m: Matrix): Matrix = Matrix(this.matrix.plus(m.matrix))
  def -(m: Matrix): Matrix = Matrix(this.matrix.minus(m.matrix))

  def *(d: Double): Matrix = Matrix(matrix.scale(d))
  def +(d: Double): Matrix = {
    val dMatrix = matrix.copy()
    dMatrix.set(d)
    Matrix(matrix.plus(dMatrix))
  }
  def -(d: Double): Matrix = {
    val dMatrix = matrix.copy()
    dMatrix.set(d)
    Matrix(matrix.minus(dMatrix))
  }

  def :*(m: Matrix) = Matrix(this.matrix.elementMult(m.matrix))

  def transpose(): Matrix = Matrix(matrix.transpose())
  def t(): Matrix = Matrix(matrix.transpose())
  def inv(): Matrix = Matrix(matrix.invert())
  def det(): Double = matrix.determinant()
  def negative(): Matrix = Matrix(matrix.negative())

  def at(index: Int) = matrix.get(index)
  def apply(row: Int, col: Int): Double = matrix.get(row, col)
  def apply(index: Int): Double = matrix.get(index)
  def column(columnIndex: Int): Matrix = Matrix(matrix.extractVector(false, columnIndex))
  def row(columnIndex: Int): Matrix = Matrix(matrix.extractVector(true, columnIndex))
  def numRows(): Int = matrix.numRows()
  def numCols(): Int = matrix.numCols()
  def size() = numRows * numCols

  def set(row: Int, col: Int, value: Double) = matrix.set(row, col, value)
  def insertIntoThis(insertRow: Int, insertCol: Int, m: Matrix) = this.matrix.insertIntoThis(insertRow, insertCol, m.matrix)
  def reshape(row: Int, col: Int): Matrix = {
    val copy = matrix.copy()
    copy.reshape(row, col)
    Matrix(copy)
  }

  def combine(insertRow: Int, insertCol: Int, m: Matrix): Matrix = {
    Matrix(this.matrix.combine(insertRow, insertCol, m.matrix))
  }

  def extractMatrix(y0: Int, y1: Int, x0: Int, x1: Int) = Matrix(this.matrix.extractMatrix(y0, y1, x0, x1))
  def extractDiag() = Matrix(this.matrix.extractDiag)

  def extractRow(rowIndex: Int) = Matrix(this.matrix.extractVector(true, rowIndex))
  def extractColumn(colIndex: Int) = Matrix(this.matrix.extractVector(false, colIndex))

  def trace():Double = this.matrix.trace()
  /**
   * Returns svd [U,W,V, rank] matrix decomposition.
   */
  def svd(): Tuple4[Matrix, Matrix, Matrix, Int] = {
    val svdMatrix = matrix.svd()
    (
      Matrix(svdMatrix.getU().asInstanceOf[SimpleMatrix]),
      Matrix(svdMatrix.getW().asInstanceOf[SimpleMatrix]),
      Matrix(svdMatrix.getV().asInstanceOf[SimpleMatrix]),
      svdMatrix.rank)
  }

  def chol(): Matrix = {

    val chol: CholeskyDecomposition[DenseMatrix64F] = DecompositionFactory.chol(this.matrix.numRows(), true)
    if (!chol.decompose(this.matrix.getMatrix())) throw new RuntimeException("Cholesky failed!")

    val L: SimpleMatrix = SimpleMatrix.wrap(chol.getT(null))

    Matrix(L)
  }

  def filterNotRow(rowIndex: Int): Matrix = {

    val newMatrix = rowIndex match {
      case 0 => matrix.extractMatrix(1, matrix.numRows, 0, matrix.numCols)
      case x if x == (matrix.numRows - 1) => matrix.extractMatrix(0, matrix.numRows - 1, 0, matrix.numCols)
      case _ => {

        val top = matrix.extractMatrix(0, rowIndex, 0, matrix.numCols)
        val bottom = matrix.extractMatrix(rowIndex + 1, matrix.numRows, 0, matrix.numCols)

        top.combine(rowIndex, 0, bottom)
      }
    }
    Matrix(newMatrix)
  }

  def filterNotColumn(columnIndex: Int): Matrix = {

    val newMatrix = columnIndex match {
      case 0 => matrix.extractMatrix(0, matrix.numRows, 1, matrix.numCols)
      case x if x == (matrix.numCols - 1) => matrix.extractMatrix(0, matrix.numRows, 0, matrix.numCols - 1)
      case _ => {

        val left = matrix.extractMatrix(0, matrix.numRows, 0, columnIndex)
        val right = matrix.extractMatrix(0, matrix.numRows, columnIndex + 1, matrix.numCols)

        left.combine(0, columnIndex, right)
      }
    }
    Matrix(newMatrix)
  }

  def filterNot(rowIndex: Int, columnIndex: Int): Matrix = filterNotRow(rowIndex).filterNotColumn(columnIndex)

  /**
   * Iterates over all matrix elements row by row.
   *
   *  @param f (rowId, colId) => Unit
   */
  def foreach(f: (Int, Int) => Unit) {
    for (rowId <- 0 until numRows; colId <- 0 until numCols) f(rowId, colId)
  }

  def toArray(): Array[Double] = matrix.getMatrix().getData()

  def copy(): Matrix = Matrix(matrix.copy())

  def isIdentical(a: Matrix, tol: Double): Boolean = {
    MatrixFeatures.isIdentical(matrix.getMatrix(), a.matrix.getMatrix(), tol);
  }

  override def toString(): String = matrix.toString
}

object Matrix {
  def apply(d: Double): Matrix = Matrix(new SimpleMatrix(Array(Array(d))))

  /**
   * Returns column vector.
   */
  def apply(columnVector: Array[Double]): Matrix = Matrix(new SimpleMatrix(Array(columnVector)).transpose())

  /**
   * Returns column vector.
   */
  def apply(d: Double*): Matrix = Matrix(new SimpleMatrix(Array(d.toArray)).transpose())

  def zeros(numRows: Int, numCols: Int): Matrix = {
    Matrix(new SimpleMatrix(numRows, numCols))
  }

  def diag(d: Double*): Matrix = Matrix(SimpleMatrix.diag(d: _*))

  def identity(width: Int): Matrix = new Matrix(SimpleMatrix.identity(width))

  /**
   * @param numRows
   * @param numCols
   * @param values Values are encoded in a row-major format
   */
  def apply(numRows: Int, numCols: Int, values: Array[Double]): Matrix = Matrix(new SimpleMatrix(numRows, numCols, true, values: _*))

  def apply(values: Array[Array[Double]]): Matrix = Matrix(new SimpleMatrix(values))

  def apply(numRows: Int, numCols: Int, cell: (Int, Int) => Double): Matrix = {
    val values = for (row <- 0 until numRows; col <- 0 until numCols) yield cell(row, col)
    Matrix(new SimpleMatrix(numRows, numCols, true, values: _*))
  }

}