package dk.bayes.infer.gp.infercovparamsem

import org.junit._
import Assert._
import com.typesafe.scalalogging.slf4j.Logging
import dk.bayes.math.linear._
import scala.math._
import dk.bayes.math.gaussian.MultivariateGaussian
import dk.bayes.infer.gp.cov.CovSEiso
import dk.bayes.dsl.variable.Gaussian
import dk.bayes.dsl.infer

/**
 * Learning Gaussian Process covariance parameters by maximising variational lower bound:
 * The EM-EP Algorithm for Gaussian Process Classification
 *  (http://mlg.eng.cam.ac.uk/zoubin/papers/ecml03.pdf, http://mlg.eng.cam.ac.uk/zoubin/papers/KimGha06-PAMI.pdf)
 *
 *  @author Daniel Korzekwa
 */
class inferCovParamsEmTest extends Logging {

  //[x,y]
  private val data = loadCSV("src/test/resources/gpml/regression_data.csv", 1)
  private val x = data.column(0)
  private val y = data.column(1)
  val logLikStdDev = -1.9025

  @Test def test {

    logger.info("Start learning GPR with EM-EP")

    //logSf, logEll
    val initParams = Array(log(1), log(1))

    val Array(finalLogSf, finalLogEll) = inferCovParamsEm(initParams, eStep, calcFPriorVar, calcFPriorVarD, tolerance = 1e-3)

    println("finalLogSf:" + finalLogSf)
    println("finalLogEll:" + finalLogEll)

    assertEquals(0.6951, finalLogSf, 0.001)
    assertEquals(-0.9745, finalLogEll, 0.001)
  }

  private def eStep(params: Array[Double]): MultivariateGaussian = {

    val m = Matrix.zeros(x.numRows(), 1)
    val v = calcFPriorVar(params)
    val fVar = dk.bayes.dsl.variable.gaussian.multivariate.MultivariateGaussian(m, v)

    val likVarMatrix = pow(exp(logLikStdDev), 2) * Matrix.identity(x.numRows())
    val yVar = Gaussian(fVar, likVarMatrix, yValue = y)

    val fPosterior = infer(fVar)

    MultivariateGaussian(fPosterior.m, fPosterior.v)
  }

  private def calcFPriorVar(params: Array[Double]): Matrix = {
    val Array(logSf, logEll) = params
    val covFunc = CovSEiso(logSf, logEll)

    val v = covFunc.cov(x) + Matrix.identity(x.numRows) * 1e-10
    v
  }

  private def calcFPriorVarD(params: Array[Double]): Array[Matrix] = {

    val Array(logSf, logEll) = params
    val covFunc = CovSEiso(logSf, logEll)

    val df_sf = covFunc.df_dSf(x)
    val df_ell = covFunc.df_dEll(x)

    Array(df_sf, df_ell)
  }

}