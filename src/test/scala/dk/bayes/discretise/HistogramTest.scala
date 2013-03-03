package dk.bayes.discretise

import org.junit._
import Assert._
import dk.bayes.testutil.AssertUtil._

class HistogramTest {

  @Test def toValues {
    assertVector(List(-10, 0, 10), Histogram(-10, 10, 3).toValues, 0.001)

    assertVector(List(-10, -3.333, 3.333, 10), Histogram(-10, 10, 4).toValues, 0.001)

    assertVector(List(-10, -8, -6, -4, -2, 0, 2, 4, 6, 8, 10), Histogram(-10, 10, 11).toValues, 0.001)
  }

  @Test def mapValues {
    assertVector(List(-25, -8.333, 8.333, 25), Histogram(-10, 10, 4).mapValues(v => v * 2.5), 0.001)
  }
}