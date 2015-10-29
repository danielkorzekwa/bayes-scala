package dk.bayes.infer.gp.gpr

import org.junit._
import Assert._
import dk.bayes.math.linear._
import scala.math._
import dk.bayes.infer.gp.cov.CovSEiso
import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector

class GenericGPRegressionTest {

  private val covFunc = CovSEiso(sf = log(7.5120), ell = log(2.1887))
  private val noiseStdDev = log(0.81075)

  @Test def test_1d_inputs ={

    val x = DenseMatrix(1.0, 2, 3)
    val y = DenseVector(1.0, 4, 9)
    val z = DenseMatrix(1.0, 2, 3, 4, 50)

    val prediction = GenericGPRegression(x, y, covFunc, noiseStdDev).predict(z)
     assertTrue(isIdentical(new DenseMatrix(2,5, Array(0.878, 1.246, 4.407, 1.123, 8.614, 1.246, 10.975, 6.063, 0, 57.087)).t, prediction,0.001))
  }

  @Test def test_perf_test ={

    val x = DenseMatrix(1d to 20 by 0.1).t
  
    val y = 2.0 * x(::,0)
    val z = DenseMatrix(3.0)

    val gp = GenericGPRegression(x, y, covFunc, noiseStdDev)
    for (i <- 1 to 100) gp.predict(z)
  }

  @Test def test_2d_inputs ={
    val x = DenseMatrix((1d, 2.0),(2d, 3.0),(3d, 4.0))

    val y = DenseVector(1d, 4, 9)
    val z = DenseMatrix((1d, 2.0),(2d, 3.0),(3d, 4.0),(4d, 5.0),(50d, 51.0))

    val prediction = GenericGPRegression(x, y, covFunc, noiseStdDev).predict(z)
    val expected  = new DenseMatrix(2, 5, Array(0.93507, 1.28129, 4.18751, 1.23829, 8.77379, 1.28129, 9.62865, 11.226, 0, 57.087)).t
    assertTrue(isIdentical(expected, prediction,0.001))
  }
}