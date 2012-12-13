package dk.bayes.gaussian
import org.ejml.simple.SimpleMatrix
import org.ejml.ops.CommonOps

/**
 * Linear Algebra.
 *
 * @author korzekwad
 */
object Linear {

  implicit def doubleToLinearDouble(d: Double): LinearDouble = LinearDouble(d)

  case class LinearDouble(d: Double) {
    def *(m: Matrix): Matrix = m * d
  }

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

    def transpose(): Matrix = Matrix(matrix.transpose())
    def inv(): Matrix = Matrix(matrix.invert())
    def det(): Double = matrix.determinant()
    def negative(): Matrix = Matrix(matrix.negative())

    def apply(row: Int, col: Int): Double = matrix.get(row, col)
    def apply(index: Int): Double = matrix.get(index)
    def column(columnIndex: Int): Matrix = Matrix(matrix.extractVector(false, columnIndex))
    def row(columnIndex: Int): Matrix = Matrix(matrix.extractVector(true, columnIndex))
    def numRows(): Int = matrix.numRows()
    def numCols(): Int = matrix.numCols()

    def set(row: Int, col: Int, value: Double) = matrix.set(row, col, value)
    def insertIntoThis(insertRow: Int, insertCol: Int, m: Matrix) = this.matrix.insertIntoThis(insertRow, insertCol, m.matrix)
    def reshape(row: Int, col: Int): Matrix = {
      val copy = matrix.copy()
      copy.reshape(row, col)
      Matrix(copy)
    }

    /**
     * Iterates over all matrix elements row by row.
     *
     *  @param f (rowId, colId) => Unit
     */
    def foreach(f: (Int, Int) => Unit) {
      for (rowId <- 0 until numRows; colId <- 0 until numCols) f(rowId, colId)
    }
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

    def apply(numRows: Int, numCols: Int): Matrix = Matrix(new SimpleMatrix(numRows, numCols))

    def apply(numRows: Int, numCols: Int, values: Array[Double]): Matrix = Matrix(new SimpleMatrix(numRows, numCols, true, values: _*))

  }
}