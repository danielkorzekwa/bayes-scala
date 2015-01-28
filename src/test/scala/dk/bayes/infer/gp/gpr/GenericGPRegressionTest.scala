package dk.bayes.infer.gp.gpr

import org.junit._
import Assert._
import dk.bayes.math.linear._
import scala.math._
import dk.bayes.infer.gp.cov.CovSEiso

class GenericGPRegressionTest {

  private val covFunc = CovSEiso(sf = log(7.5120), ell = log(2.1887))
  private val noiseStdDev = log(0.81075)

  @Test def test_1d_inputs {

    val x = Matrix(1, 2, 3)
    val y = Matrix(1, 4, 9)
    val z = Matrix(1, 2, 3, 4, 50)

    val prediction = GenericGPRegression(x, y, covFunc, noiseStdDev).predict(z)
    assertEquals(Matrix(5, 2, Array(0.878, 1.246, 4.407, 1.123, 8.614, 1.246, 10.975, 6.063, 0, 57.087)).toString, prediction.toString)
  }

  @Test def test_perf_test {

    val x = Matrix((1d to 20 by 0.1): _*)
    val y = 2 * x
    val z = Matrix(3)

    val gp = GenericGPRegression(x, y, covFunc, noiseStdDev)
    for (i <- 1 to 100) gp.predict(z)
  }

  @Test def test_2d_inputs {

    val x = Matrix(Array(
      Array(1d, 2),
      Array(2d, 3),
      Array(3d, 4)))

    val y = Matrix(1, 4, 9)
    val z = Matrix(Array(
      Array(1d, 2),
      Array(2d, 3),
      Array(3d, 4),
      Array(4d, 5),
      Array(50d, 51)))

    val prediction = GenericGPRegression(x, y, covFunc, noiseStdDev).predict(z)
    assertEquals(Matrix(5, 2, Array(0.93507, 1.28129, 4.18751, 1.23829, 8.77379, 1.28129, 9.62865, 11.226, 0, 57.087)).toString, prediction.toString)
  }
}