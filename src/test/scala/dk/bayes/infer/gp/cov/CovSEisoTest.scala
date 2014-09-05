package dk.bayes.infer.gp.cov

import org.junit._
import Assert._
import scala.math._
import dk.bayes.math.linear.Matrix

class CovSEisoTest {

	 private val covFunc = new CovSEiso(sf = log(2), log(10))

  @Test def test_1D {

    assertEquals(4, covFunc.cov(Matrix(3), Matrix(3)), 0.0001)
    assertEquals(3.8239, covFunc.cov(Matrix(2), Matrix(5)), 0.0001)
    assertEquals(2.9045, covFunc.cov(Matrix(2), Matrix(10)), 0.0001)
     assertEquals(1.3181,  new CovSEiso(sf = log(2), log(200)).cov(Matrix(2), Matrix(300)), 0.0001)
  }

  @Test def perf_test {

    val x1 = Matrix(10)
    val x2 = Matrix(2)
    (1L to 2000L * 50 * 50).foreach(_ => covFunc.cov(x1, x2))

  }
}