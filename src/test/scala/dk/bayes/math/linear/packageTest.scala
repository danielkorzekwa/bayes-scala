package dk.bayes.math.linear

import org.junit._
import Assert._

class packageTest {

  @Test def load_from_csv {
    val file = "src/test/resources/sprinkler_data/sprinkler_10k_samples_5pct_missing_values.dat"

    val matrix = loadCSV(file, 1)
    assertEquals(10000, matrix.numRows)
    assertEquals(5, matrix.numCols)
    assertEquals(Array(1.0, 1, 0, 1, 0).toList, matrix.row(10).toArray.toList)

  }
}