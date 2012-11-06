package dk.bayes.em

import org.junit._
import Assert._
import dk.bayes.testutil.SprinklerBN._

class DataSetTest {

  @Test def fromFile {

    val variableIds = Array(winterVar.id, rainVar.id, sprinklerVar.id, slipperyRoadVar.id, wetGrassVar.id)
    val dataSet = DataSet.fromFile("src/test/resources/sprinkler_data/sprinkler_10k_samples_5pct_missing_values.dat", variableIds)

    assertEquals(10000, dataSet.samples.size)

    assertEquals(List(0, 0, 1, 0, 0), dataSet.samples(0).toList)
    assertEquals(List(0, 0, 1, 0, -1), dataSet.samples(1).toList)
    assertEquals(List(0, 0, -1, 1, 0), dataSet.samples(9998).toList)
    assertEquals(List(0, 0, 1, 0, 0), dataSet.samples(9999).toList)
  }
}
