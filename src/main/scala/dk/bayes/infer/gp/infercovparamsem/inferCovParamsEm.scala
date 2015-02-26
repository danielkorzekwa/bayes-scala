package dk.bayes.infer.gp.infercovparamsem

import com.typesafe.scalalogging.slf4j.LazyLogging
import dk.bayes.math.gaussian.MultivariateGaussian
import dk.bayes.math.linear.Matrix
import scala.math._
import scala.annotation.tailrec

/**
 * Learning Gaussian Process covariance parameters by maximising variational lower bound:
 * The EM-EP Algorithm for Gaussian Process Classification
 *  (http://mlg.eng.cam.ac.uk/zoubin/papers/ecml03.pdf, http://mlg.eng.cam.ac.uk/zoubin/papers/KimGha06-PAMI.pdf)
 *
 *  @author Daniel Korzekwa
 */
object inferCovParamsEm extends LazyLogging {

  /**
   * Returns learned parameters.
   *
   * GP model x -> f -> y
   *
   * @param initialParams
   * @param eStep (params) => fPosterior
   * @param calcFPriorVar (params) => f prior variance
   * @param calcFPriorVarD (params => derivatives of f prior variance with respect to hyper parameters
   * @param currentIterParams Learned parameters at the current iteration
   */
  def apply(initialParams: Array[Double],
            eStep: (Array[Double]) => MultivariateGaussian, calcFPriorVar: (Array[Double]) => Matrix, calcFPriorVarD: (Array[Double]) => Array[Matrix],
            maxIter: Int = 100, tolerance: Double = 1e-6): Array[Double] = {

    @tailrec
    def emIter(currIter: Int, currParams: Array[Double]): Array[Double] = {

      if (currIter >= maxIter) {
        logger.warn(s"EM-EP did not converge in less than ${maxIter} iterations")
        return currParams
      }

      val fPosterior = eStep(currParams)

      val newParams = mStep(fPosterior, currParams, calcFPriorVar, calcFPriorVarD)
      logger.info("Iter=%d, New params: %s".format(currIter, currParams.toList))

      if (isIdentical(newParams, currParams, tolerance)) return newParams else emIter(currIter + 1, newParams)
    }

    logger.info("Initial params: %s".format(initialParams.toList))

    val finalParams = emIter(currIter = 0, initialParams)

    finalParams
  }

  private def isIdentical(x1: Array[Double], x2: Array[Double], tolerance: Double): Boolean = {

    val notIdentical = x1.zip(x2).find { case (x1, x2) => abs(x1 - x2) > tolerance }

    notIdentical.isEmpty
  }
}