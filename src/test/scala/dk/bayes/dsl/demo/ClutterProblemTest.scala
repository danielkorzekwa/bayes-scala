package dk.bayes.dsl.demo

import org.junit._
import Assert._
import dk.bayes.dsl.variable.Gaussian
import dk.bayes.dsl.demo.variables.ClutteredGaussian
import dk.bayes.dsl.infer

/**
 * References for the Clutter problem:
 * - Tom Minka thesis (http://research.microsoft.com/en-us/um/people/minka/papers/ep/minka-thesis.pdf)
 *  - Christopher M. Bishop. Pattern Recognition and Machine Learning (Information Science and Statistics), 2009, chapter 10.7.1, page 511
 */
class ClutterProblemTest {

  @Test def test {

    val x = Gaussian(15, 100)
    val y1 = ClutteredGaussian(x, w = 0.4, a = 10, value = 3)
    val y2 = ClutteredGaussian(x, w = 0.4, a = 10, value = 5)

    val posteriorX = infer(x)
    assertEquals(4.3431, posteriorX.m, 0.0001)
    assertEquals(4.3163, posteriorX.v, 0.0001)

  }

}
