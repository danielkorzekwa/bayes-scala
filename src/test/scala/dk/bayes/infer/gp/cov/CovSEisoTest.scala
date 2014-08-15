package dk.bayes.infer.gp.cov

import org.junit._
import Assert._
import scala.math._
import dk.bayes.math.linear.Matrix

class CovSEisoTest {

  private val covFunc = new CovSEiso(sf = log(1), log(10))

  @Test def perf_test {

    val x1 = Matrix(10)
    val x2 = Matrix(2)
    (1L to 2000L * 50 * 50).foreach(_ => covFunc.cov(x1, x2))

  }
}