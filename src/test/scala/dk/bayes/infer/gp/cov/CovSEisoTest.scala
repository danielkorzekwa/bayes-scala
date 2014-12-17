package dk.bayes.infer.gp.cov

import org.junit._
import Assert._
import scala.math._
import dk.bayes.math.linear.Matrix
import scala.util.Random

class CovSEisoTest {

  private val covFunc = new CovSEiso(sf = log(2), log(10))

  /**
   * F
   * Tests for cov()
   */

  @Test def test_1D_cov {

    assertEquals(4, covFunc.cov(Matrix(3), Matrix(3)), 0.0001)
    assertEquals(3.8239, covFunc.cov(Matrix(2), Matrix(5)), 0.0001)
    assertEquals(2.9045, covFunc.cov(Matrix(2), Matrix(10)), 0.0001)
    assertEquals(1.3181, new CovSEiso(sf = log(2), log(200)).cov(Matrix(2), Matrix(300)), 0.0001)
  }

  @Test def multi_dim_cov {

    val x1 = Matrix(Array.fill(4)(1d))
    val x2 = Matrix(Array.fill(4)(2d))

    val covValue = covFunc.cov(x1, x2)
    assertEquals(3.92079, covValue, 0.00001)
  }

  @Test def multi_dim_cov_2 {
    val rand = new Random(4656)
    val n = 2000
    val x1 = Array.fill(n)(rand.nextDouble)
    val x2 = Array.fill(n)(rand.nextDouble)

    val covValue = covFunc.cov(Matrix(x1), Matrix(x2))
    assertEquals(0.71287, covValue, 0.00001)
  }

  @Test def perf_test_1d_cov {

    val x1 = Matrix(10)
    val x2 = Matrix(2)
    (1L to 2000L * 50 * 50).foreach(_ => covFunc.cov(x1, x2))

  }

  /**
   * Tests for df_dSf
   *
   */
  @Test def test_1D_df_dSf {

    assertEquals(8, covFunc.df_dSf(Matrix(3), Matrix(3)), 0.0001)
    assertEquals(7.6479, covFunc.df_dSf(Matrix(2), Matrix(5)), 0.0001)
    assertEquals(5.8091, covFunc.df_dSf(Matrix(2), Matrix(10)), 0.0001)
    assertEquals(2.6363, new CovSEiso(sf = log(2), log(200)).df_dSf(Matrix(2), Matrix(300)), 0.0001)
  }

  @Test def multi_dim_df_dSf {
    val rand = new Random(4656)
    val n = 2000
    val x1 = Array.fill(n)(rand.nextDouble)
    val x2 = Array.fill(n)(rand.nextDouble)

    val covValue = covFunc.df_dSf(Matrix(x1), Matrix(x2))
    assertEquals(1.4257, covValue, 0.0001)
  }

  @Test def perf_test_1d_df_dSf {

    val x1 = Matrix(10)
    val x2 = Matrix(2)
    (1L to 2000L * 50 * 50).foreach(_ => covFunc.df_dSf(x1, x2))

  }

  /**
   * Tests for df_dEll
   */
  @Test def test_1D_df_dEll {

    assertEquals(0, covFunc.df_dEll(Matrix(3), Matrix(3)), 0.0001)
    assertEquals(0.3441, covFunc.df_dEll(Matrix(2), Matrix(5)), 0.0001)
    assertEquals(1.8589, covFunc.df_dEll(Matrix(2), Matrix(10)), 0.0001)
    assertEquals(2.9264, new CovSEiso(sf = log(2), log(200)).df_dEll(Matrix(2), Matrix(300)), 0.0001)
  }

  @Test def multi_dim_df_dEll {
    val rand = new Random(4656)
    val n = 2000
    val x1 = Array.fill(n)(rand.nextDouble)
    val x2 = Array.fill(n)(rand.nextDouble)

    val covValue = covFunc.df_dEll(Matrix(x1), Matrix(x2))
    assertEquals(2.4590, covValue, 0.0001)
  }

  @Test def perf_test_1d_df_dEll {

    val x1 = Matrix(10)
    val x2 = Matrix(2)
    (1L to 200L * 50 * 50).foreach(_ => covFunc.df_dEll(x1, x2))

  }
  
  /**
   * Perf test for cov(Double,Double)
   */

  @Test def perf_cov_Double_Double {
    val x1=10
    val x2=200
    
    for(i <- 1 to 4000000) covFunc.cov(x1,x2)
  }
}