package dk.bayes.learn.lds

import scala.math._
import dk.bayes.model.factor.BivariateGaussianFactor
import dk.bayes.math.gaussian.canonical.CanonicalGaussian
import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian

/**
 * Learning parameters in Linear Dynamic Systems.
 *
 * Based on:
 * Zoubin Ghahramani, Geoffrey E. Hinton. Parameter Estimation for Linear Dynamical Systems, 1996
 * http://www.gatsby.ucl.ac.uk/~zoubin/papers/tr-96-2.pdf
 */
trait LDSMStep {

  /**
   * M-step
   */

  /**
   * @param sStats Sufficient statistics. Seq of Tuple2[marginal of prior factor, observed value]
   */
  def newC(sStats: IndexedSeq[Tuple2[DenseCanonicalGaussian, Double]]): Double

  /**
   * @param sStats Sufficient statistics. Seq of Tuple2[marginal of prior factor, observed value]
   */
  def newR(sStats: IndexedSeq[Tuple2[DenseCanonicalGaussian, Double]]): Double

  /**
   * @param sStats Sufficient statistics, marginals of transition factors (time t-1, time t)
   */
  def newA(sStats: IndexedSeq[DenseCanonicalGaussian]): Double

  /**
   * @param sStats Sufficient statistics, marginals of transition factors (time t-1, time t)
   */
  def newQ(sStats: IndexedSeq[DenseCanonicalGaussian]): Double

  /**
   * @param sStats Sufficient statistics, marginals of prior factors
   */
  def newPi(sStats: IndexedSeq[DenseCanonicalGaussian]): Double

  /**
   * @param sStats Sufficient statistics, marginals of prior factors
   */
  def newV(sStats: IndexedSeq[DenseCanonicalGaussian]): Double
}