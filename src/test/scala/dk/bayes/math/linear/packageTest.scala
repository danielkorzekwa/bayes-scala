package dk.bayes.math.linear

import org.junit._
import Assert._

class packageTest {

  @Test def number_matrix_* {
    val m = Matrix(3, 2, Array(1d, 2, 3, 4, 5, 6))
    val expected = Matrix(3, 2, Array(2d, 4, 6, 8, 10, 12))
    assertEquals(expected.toString, (2 * m).toString)
  }

  @Test def load_from_csv {
    val file = "src/test/resources/sprinkler_data/sprinkler_10k_samples_5pct_missing_values.dat"

    val matrix = loadCSV(file, 1)
    assertEquals(10000, matrix.numRows)
    assertEquals(5, matrix.numCols)
    assertEquals(Array(1.0, 1, 0, 1, 0).toList, matrix.row(10).toArray.toList)

  }

  @Test def test_woodbury {
    val Ainv = Matrix(2, 2, Array(2.3, 0.6, 0.6, 4.5))
    val (u, w, v,rank) = Matrix(2, 2, Array(2, 0.4, 0.4, 5)).svd

    val result = woodbury(Ainv, u, w.inv, v)

    assertEquals(Matrix(2, 2, Array(0.411801, -0.026765, -0.026765, 0.192936)).toString, result.toString)
  }
}