package dk.bayes.dsl.demo

import org.junit._
import Assert._
import scala.math._
import dk.bayes.math.linear.Matrix
import dk.bayes.dsl.variable.Gaussian
import dk.bayes.dsl.InferEngine
import dk.bayes.dsl.infer
import dk.bayes.dsl.variable.Categorical
import dk.bayes.testutil.AssertUtil._

class TrueSkillTwoPlayersTest {

  val skill1 = Gaussian(4, 81)
  val skill2 = Gaussian(41, 25)

  val perf1 = Gaussian(skill1, pow(25d / 6, 2))
  val perf2 = Gaussian(skill2, pow(25d / 6, 2))

  val perfDiff = Gaussian(A = Matrix(1.0, -1), Vector(perf1, perf2), v = 0.0)
  val outcomeFactor = Categorical(perfDiff, cdfThreshold = 0, value = 0) //player 1 is a winner

  @Test def test {
    
    val skill1Marginal = infer(skill1)
    assertEquals(27.1744, skill1Marginal.m, 0.0001)
    assertEquals(37.4973, skill1Marginal.v, 0.0001)
  }

}