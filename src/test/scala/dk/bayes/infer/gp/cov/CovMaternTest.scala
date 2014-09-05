package dk.bayes.infer.gp.cov

import org.junit._
import Assert._
import scala.math._
import dk.bayes.math.linear.Matrix

class CovMaternTest {

  val covFunc = CovMatern52(sf = log(4), ell = log(10))
  @Test def cov {
    assertEquals(14.8954, covFunc.cov(Matrix(7), Matrix(10)), 0.0001)
    assertEquals(3.2947, covFunc.cov(Matrix(7), Matrix(24.3)), 0.0001)
  }

  @Test def cov_df_dsf {
    assertEquals(29.7908, covFunc.df_dSf(Matrix(7), Matrix(10)), 0.0001)
    assertEquals(6.5895, covFunc.df_dSf(Matrix(7), Matrix(24.3)), 0.0001)
    assertEquals(31.9999, covFunc.df_dSf(Matrix(7), Matrix(7)), 0.0001)
  }
  @Test def cov_df_dEll {

    assertEquals(2.0502, covFunc.df_dEll(Matrix(7), Matrix(10)), 0.0001)
    assertEquals(8.1175, covFunc.df_dEll(Matrix(7), Matrix(24.3)), 0.0001)
    assertEquals(2.6666e-11, covFunc.df_dEll(Matrix(0), Matrix(0.00001)), 0.0001)
    assertEquals(0, covFunc.df_dEll(Matrix(0), Matrix(1e-10)), 0.0001)
    assertEquals(0, covFunc.df_dEll(Matrix(0), Matrix(0)), 0.0001)
  }
}