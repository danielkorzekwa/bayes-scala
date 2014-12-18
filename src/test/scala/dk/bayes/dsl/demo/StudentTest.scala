package dk.bayes.dsl.demo

import org.junit._
import Assert._
import dk.bayes.dsl.variable.Categorical
import dk.bayes.dsl.infer
import dk.bayes.testutil.AssertUtil._
class StudentTest {

  val difficulty = Categorical(Vector(0.6, 0.4))
  val intelli = Categorical(Vector(0.7, 0.3))
  val grade = Categorical(intelli, difficulty, Vector(0.3, 0.4, 0.3, 0.05, 0.25, 0.7, 0.9, 0.08, 0.02, 0.5, 0.3, 0.2))
  val sat = Categorical(intelli, Vector(0.95, 0.05, 0.2, 0.8))
  val letter = Categorical(grade, Vector(0.1, 0.9, 0.4, 0.6, 0.99, 0.01))

  /**
   * Tests for marginal() method
   */

  @Test def marginal {

    assertVector(Array(0.6, 0.4), infer(difficulty).cpd, 0.0001)
    assertVector(Array(0.7, 0.3), infer(intelli).cpd, 0.0001)
    assertVector(Array(0.3620, 0.2884, 0.3496), infer(grade).cpd, 0.0001)
    assertVector(Array(0.725, 0.275), infer(sat).cpd, 0.0001)
    assertVector(Array(0.4976, 0.5023), infer(letter).cpd, 0.0001)

  }

  @Test def marginal_given_sat_is_high {

    sat.setValue(0)

    assertVector(Array(0.6, 0.4), infer(difficulty).cpd, 0.0001)
    assertVector(Array(0.9172, 0.0827), infer(intelli).cpd, 0.0001)
    assertVector(Array(0.2446, 0.3257, 0.4295), infer(grade).cpd, 0.0001)
    assertVector(Array(1d, 0), infer(sat).cpd, 0.0001)
    assertVector(Array(0.58, 0.4199), infer(letter).cpd, 0.0001)

  }

}