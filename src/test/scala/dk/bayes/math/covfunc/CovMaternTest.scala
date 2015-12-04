package dk.bayes.math.covfunc

import org.junit._
import org.junit.Assert._
import scala.math._
import breeze.linalg.DenseVector
import dk.bayes.math.covfunc.CovMatern52

class CovMaternTest {

  val covFunc = CovMatern52(sf = log(4), ell = log(10))
  @Test def cov = {
    assertEquals(14.8954, covFunc.cov(Array(7d), Array(10d)), 0.0001)
    assertEquals(3.2947, covFunc.cov(Array(7d), Array(24.3)), 0.0001)
  }

  @Test def cov_df_dsf = {
    assertEquals(29.7908, covFunc.df_dSf(Array(7), Array(10)), 0.0001)
    assertEquals(6.5895, covFunc.df_dSf(Array(7), Array(24.3)), 0.0001)
    assertEquals(31.9999, covFunc.df_dSf(Array(7), Array(7)), 0.0001)
  }
  @Test def cov_df_dEll = {

    assertEquals(2.0502, covFunc.df_dEll(DenseVector(7), DenseVector(10)), 0.0001)
    assertEquals(8.1175, covFunc.df_dEll(DenseVector(7), DenseVector(24.3)), 0.0001)
    assertEquals(2.6666e-11, covFunc.df_dEll(DenseVector(0), DenseVector(0.00001)), 0.0001)
    assertEquals(0, covFunc.df_dEll(DenseVector(0), DenseVector(1e-10)), 0.0001)
    assertEquals(0, covFunc.df_dEll(DenseVector(0), DenseVector(0)), 0.0001)
  }
}