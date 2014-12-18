package dk.bayes.model.factor

import org.junit._
import Assert._

class SingleTableFactorTest {

  @Test def equals {
    val f1 = SingleTableFactor(7, 2, Array(1.0, 0))
    val f2 = SingleTableFactor(7, 2, Array(1.0, Double.NaN))
    val f3 = SingleTableFactor(7, 2, Array(1.0, 1.0))

    assertEquals(false, f1.equals(f3, 0.00001))
    assertEquals(false, f1.equals(f2, 0.00001))
    assertEquals(true, f1.equals(f1, 0.00001))
  }

}