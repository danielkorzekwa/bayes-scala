package dk.bayes.math.linear

import org.junit._
import org.junit.Assert._

import breeze.linalg.DenseMatrix

object hasUncountableTest {

  @Test def test = {

    assertEquals(false, new DenseMatrix(2, 2, Array(1.0, 2, 3, 4)))
    assertEquals(true, new DenseMatrix(2, 2, Array(1.0, Double.NaN, 3, 4)))
    assertEquals(true, new DenseMatrix(2, 2, Array(1.0, 2, Double.PositiveInfinity, 4)))
    assertEquals(true, new DenseMatrix(2, 2, Array(1.0, 2, 3, Double.NegativeInfinity)))

  }
}