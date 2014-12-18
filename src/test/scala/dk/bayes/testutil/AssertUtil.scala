package dk.bayes.testutil

import org.junit._
import Assert._
import dk.bayes.model.clustergraph.factor._
import dk.bayes.math.gaussian.Gaussian

object AssertUtil {

  def assertVector(expected: Seq[Double], actual: Seq[Double], delta: Double) {
    assertEquals("Wrong size of vector.".format(expected.size, actual.size), expected.size, actual.size)
    var i = 0
    for ((expected, actual) <- expected.zip(actual)) {
      assertEquals("Element: " + i, expected, actual, delta)
      i += 1
    }
  }

  def assertFactor(expected: Factor, actual: Factor, delta: Double = 0) {
    assertEquals(expected.getVariables().toList, actual.getVariables().toList)
    assertVector(expected.getValues().toList, actual.getValues().toList, delta)
  }

  def assertGaussian(expected: Gaussian, actual: Gaussian, delta: Double = 0) {
    assertEquals(expected.m, actual.m, delta)
    assertEquals(expected.v, actual.v, delta)
  }
}