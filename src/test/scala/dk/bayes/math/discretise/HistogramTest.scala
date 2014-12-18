package dk.bayes.math.discretise

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

  @Test(expected = classOf[IllegalArgumentException]) def valueOf_below_range {
    Histogram(-10, 10, 4).valueOf(-1)
  }

  @Test(expected = classOf[IllegalArgumentException]) def valueOf_above_range {
    Histogram(-10, 10, 4).valueOf(5)
  }

  @Test def valueOf {
    assertEquals(-10, Histogram(-10, 10, 4).valueOf(0), 0)
    assertEquals(-3.333, Histogram(-10, 10, 4).valueOf(1), 0.001)
    assertEquals(3.333, Histogram(-10, 10, 4).valueOf(2), 0.001)
    assertEquals(10, Histogram(-10, 10, 4).valueOf(3), 0)
  }

  @Test(expected = classOf[IllegalArgumentException]) def binIndexOf_below_range {
    Histogram(-10, 10, 4).binIndexOf(-10.1)
  }

  @Test(expected = classOf[IllegalArgumentException]) def binIndexOf_above_range {
    Histogram(-10, 10, 4).binIndexOf(10.1)
  }

  @Test def binIndexOf {
    assertEquals(0, Histogram(-10, 10, 4).binIndexOf(-10))
    assertEquals(1, Histogram(-10, 10, 4).binIndexOf(-9.999))
    assertEquals(1, Histogram(-10, 10, 4).binIndexOf(-8.64))
    assertEquals(1, Histogram(-10, 10, 4).binIndexOf(-3.334))
    assertEquals(2, Histogram(-10, 10, 4).binIndexOf(-3.333))
    assertEquals(2, Histogram(-10, 10, 4).binIndexOf(-3.332))
    assertEquals(2, Histogram(-10, 10, 4).binIndexOf(-0.1))
    assertEquals(2, Histogram(-10, 10, 4).binIndexOf(0))
    assertEquals(2, Histogram(-10, 10, 4).binIndexOf(1))
    assertEquals(2, Histogram(-10, 10, 4).binIndexOf(3.332))
    assertEquals(2, Histogram(-10, 10, 4).binIndexOf(3.333))
    assertEquals(3, Histogram(-10, 10, 4).binIndexOf(3.334))
    assertEquals(3, Histogram(-10, 10, 4).binIndexOf(7.54))
    assertEquals(3, Histogram(-10, 10, 4).binIndexOf(9.99))
    assertEquals(3, Histogram(-10, 10, 4).binIndexOf(10))
  }
}