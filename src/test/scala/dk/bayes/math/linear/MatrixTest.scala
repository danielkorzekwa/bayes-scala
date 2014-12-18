package dk.bayes.math.linear

import org.junit._
import Assert._

class MatrixTest {

  @Test def matrix_matrix_* {
    val m1 = Matrix(3, 2, Array(1d, 2, 3, 4, 5, 6))
    val m2 = Matrix(2, 1, Array(0.5, 1.5d))
    val expected = Matrix(3, 1, Array(3.5, 7.5, 11.5))
    assertEquals(expected.toString, (m1 * m2).toString)
  }

  @Test def matrix_matrix_+ {
    val m1 = Matrix(3, 2, Array(1d, 2, 3, 4, 5, 6))
    val m2 = Matrix(3, 2, Array(2d, 3, 4, 5, 6, 7))
    val expected = Matrix(3, 2, Array(3d, 5, 7, 9, 11, 13))
    assertEquals(expected.toString, (m1 + m2).toString)
  }

  @Test def matrix_matrix_- {
    val m1 = Matrix(3, 2, Array(1d, 2, 3, 4, 5, 6))
    val m2 = Matrix(3, 2, Array(2d, 3, 4, 5, 6, 7))
    val expected = Matrix(3, 2, Array(-1d, -1, -1, -1, -1, -1))
    assertEquals(expected.toString, (m1 - m2).toString)
  }

  @Test def matrix_number_* {
    val m = Matrix(3, 2, Array(0.5, 1.2, 3, 4, 5, 6))
    val expected = Matrix(3, 2, Array(1, 2.4, 6, 8, 10, 12))
    assertEquals(expected.toString, (m * 2).toString)
  }

  @Test def matrix_number_+ {
    val m = Matrix(3, 2, Array(0.5, 1.2, 3, 4, 5, 6))
    val expected = Matrix(3, 2, Array(2.5, 3.2, 5, 6, 7, 8))
    assertEquals(expected.toString, (m + 2).toString)
  }

  @Test def matrix_number_- {
    val m = Matrix(3, 2, Array(0.5, 1.2, 3, 4, 5, 6))
    val expected = Matrix(3, 2, Array(-1.5, -0.8, 1, 2, 3, 4))
    assertEquals(expected.toString, (m - 2).toString)
  }

  @Test def matrix_transpose {
    val m = Matrix(3, 2, Array(0.5, 1.2, 3, 4, 5, 6))
    val expected = Matrix(2, 3, Array(0.5, 3, 5, 1.2, 4, 6))
    assertEquals(expected.toString, m.transpose.toString)
  }

  @Test def matrix_inv {
    val m = Matrix(2, 2, Array(0.5, 1.2, 3, 4))
    val expected = Matrix(2, 2, Array(-2.5, 0.75, 1.875, -0.3125))
    assertEquals(expected.toString, m.inv.toString)
  }

  @Test def matrix_det {
    val m = Matrix(2, 2, Array(0.5, 1.2, 3, 4))
    val expected = Matrix(2, 2, Array(-2.5, 0.75, 1.875, -0.3125))
    assertEquals(-1.6, m.det, 0.0001)
  }

  @Test def matrix_negative {
    val m = Matrix(3, 2, Array(0.5, 1.2, 3, 4, -5, -6))
    val expected = Matrix(3, 2, Array(-0.5, -1.2, -3, -4, 5, 6))
    assertEquals(expected.toString, m.negative.toString)
  }

  @Test def matrix_apply_row_col {
    val m = Matrix(3, 2, Array(0.5, 1.2, 3, 4, -5, -6))
    assertEquals(3, m(1, 0), 0)
  }

  @Test def matrix_apply_index {
    val m = Matrix(3, 2, Array(0.5, 1.2, 3, 4, -5, -6))
    assertEquals(-5, m(4), 0)
  }

  @Test def matrix_column {
    val m = Matrix(3, 2, Array(0.5, 1.2, 3, 4, -5, -6))
    val expected = Matrix(3, 1, Array(1.2, 4, -6))
    assertEquals(expected.toString, m.column(1).toString)
  }

  @Test def matrix_row {
    val m = Matrix(3, 2, Array(0.5, 1.2, 3, 4, -5, -6))
    val expected = Matrix(1, 2, Array(3d, 4))
    assertEquals(expected.toString, m.row(1).toString)

  }

  @Test def matrix_numRows {
    val m = Matrix(3, 2, Array(0.5, 1.2, 3, 4, -5, -6))
    assertEquals(3, m.numRows)
  }

  @Test def matrix_numCols {
    val m = Matrix(3, 2, Array(0.5, 1.2, 3, 4, -5, -6))
    assertEquals(2, m.numCols)
  }

  @Test def matrix_set {
    val m = Matrix(3, 2, Array(0.5, 1.2, 3, 4, -5, -6))
    m.set(2, 1, 0.3)
    val expected = Matrix(3, 2, Array(0.5, 1.2, 3, 4, -5, 0.3))
    assertEquals(expected.toString, m.toString)
  }

  @Test def matrix_insertIntoThis {
    val m = Matrix(3, 2, Array(0.5, 1.2, 3, 4, -5, -6))
    m.insertIntoThis(1, 0, Matrix(2, 2, Array(0.1, 0.2, 0.3, 0.4)))
    val expected = Matrix(3, 2, Array(0.5, 1.2, 0.1, 0.2, 0.3, 0.4))
    assertEquals(expected.toString, m.toString)
  }

  @Test def matrix_reshape {
    val m = Matrix(1, 6, Array(0.5, 1.2, 3, 4, -5, -6))
    val expected = Matrix(3, 2, Array(0.5, 1.2, 3, 4, -5, -6))
    assertEquals(expected.toString, m.reshape(3, 2).toString)
  }

  @Test def matrix_foreach {
    val m = Matrix(3, 2, Array(0.5, 1.2, 3, 4, -5, -6))

    val newM = Matrix.zeros(3, 2)
    m.foreach((rowId, colId) => newM.set(rowId, colId, m(rowId, colId)))

    assertEquals(newM.toString, m.toString)
  }

  /**
   * Tests for filterRowNot
   */

  @Test def matrix_filterRowNot_first_index {
    assertEquals(Matrix(2d, 3).toString, Matrix(1.5, 2, 3).filterNotRow(0).toString)
    assertEquals(Matrix(2, 2, Array(3d, 4, 5, 6)).toString, Matrix(3, 2, Array(1d, 2, 3, 4, 5, 6)).filterNotRow(0).toString)
  }

  @Test def matrix_filterRowNot_middle_index {
    assertEquals(Matrix(1.5d, 3).toString, Matrix(1.5, 2, 3).filterNotRow(1).toString)
    assertEquals(Matrix(2, 2, Array(1d, 2, 5, 6)).toString, Matrix(3, 2, Array(1d, 2, 3, 4, 5, 6)).filterNotRow(1).toString)
  }

  @Test def matrix_filterRowNot_last_index {
    assertEquals(Matrix(1.5, 2).toString, Matrix(1.5, 2, 3).filterNotRow(2).toString)
    assertEquals(Matrix(2, 2, Array(1d, 2, 3, 4)).toString, Matrix(3, 2, Array(1d, 2, 3, 4, 5, 6)).filterNotRow(2).toString)
  }

  /**
   * Tests for filterColumnNot
   */

  @Test def matrix_filterColumnNot_first_index {

    assertEquals(Matrix(2d, 3).transpose.toString, Matrix(1.5, 2, 3).transpose.filterNotColumn(0).toString)
    assertEquals(Matrix(2, 2, Array(2d, 3, 5, 6)).toString, Matrix(2, 3, Array(1d, 2, 3, 4, 5, 6)).filterNotColumn(0).toString)
  }

  @Test def matrix_filterColumnNot_middle_index {
    assertEquals(Matrix(1.5d, 3).transpose.toString, Matrix(1.5, 2, 3).transpose.filterNotColumn(1).toString)
    assertEquals(Matrix(2, 2, Array(1d, 3, 4, 6)).toString, Matrix(2, 3, Array(1d, 2, 3, 4, 5, 6)).filterNotColumn(1).toString)
  }

  @Test def matrix_filterColumnNot_last_index {
    assertEquals(Matrix(1.5, 2).transpose.toString, Matrix(1.5, 2, 3).transpose.filterNotColumn(2).toString)
    assertEquals(Matrix(2, 2, Array(1d, 2, 4, 5)).toString, Matrix(2, 3, Array(1d, 2, 3, 4, 5, 6)).filterNotColumn(2).toString)
  }

  /**
   * Tests for filterNot
   */
  @Test def matrix_filterNot {
    assertEquals(Matrix(1d, 5).toString, Matrix(3, 2, Array(1d, 2, 3, 4, 5, 6)).filterNot(1, 1).toString)
  }

  /**
   * Tests for Matrix object
   */

  @Test def apply {
    assertEquals(Matrix(2.3).toString, Matrix(Array(2.3)).toString)
    assertEquals(Matrix(2.3, 4.5).toString, Matrix(Array(2.3, 4.5)).toString)
    assertEquals(Matrix(2.3, 4.5).toString, Matrix(2, 1, Array(2.3, 4.5)).toString)
    assertEquals(Matrix(2, 2, Array(0d, 0, 0, 0)).toString, Matrix.zeros(2, 2).toString)
    assertEquals(Matrix(2, 2, Array(0d, 0, 0, 1)).toString, Matrix(2, 2, (row: Int, col: Int) => row * col).toString)
  }

  @Test def svd {
    val matrix = Matrix(2, 2, Array(2, 0.4, 0.4, 5))
    val (u, w, v,rank) = matrix.svd

    assertEquals(Matrix(2, 2, Array(0.12993, 0.99152, 0.99152, -0.12993)).toString, u.toString)
    assertEquals(Matrix(2, 2, Array(5.0524, 0, 0, 1.9476)).toString, w.toString)
    assertEquals(Matrix(2, 2, Array(0.12993, 0.99152, 0.99152, -0.12993)).toString, v.toString)
    assertEquals(2,rank)
  }

}