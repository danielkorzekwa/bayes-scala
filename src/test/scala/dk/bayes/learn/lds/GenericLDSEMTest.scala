package dk.bayes.learn.lds

import org.junit._
import Assert._
import dk.bayes.math.gaussian.Gaussian

class GenericLDSEMTest {

  @Test def test_data {
    val data = Array(
      Array(2d, 4, 6),
      Array(20d, 40, 60),
      Array(3.5))

    val emSummary = GenericLDSEM.learn(data, priorMean = Gaussian(0, 1), emissionVar = 1, iterNum = 10)

    assertEquals(17.2381, emSummary.priorMean.m, 0.0001)
    assertEquals(226.7098, emSummary.priorMean.v, 0.0001)
    assertEquals(193.7269, emSummary.emissionVar, 0.0001)

  }
}