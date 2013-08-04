package dk.bayes.learn.lds

import scala.math._
import dk.bayes.model.factor.BivariateGaussianFactor

/**
 * Learning parameters in Linear Dynamic Systems.
 *
 * Based on:
 * Zoubin Ghahramani, Geoffrey E. Hinton. Parameter Estimation for Linear Dynamical Systems, 1996
 * http://www.gatsby.ucl.ac.uk/~zoubin/papers/tr-96-2.pdf
 */
trait LDSLearn {

  /**
   * M-step
   */

  /**
   * @param sStats Sufficient statistics, marginals of transition factors (time t-1, time t)
   */
  def newA(sStats: IndexedSeq[TransitionStat]): Double

  /**
   * @param sStats Sufficient statistics, marginals of transition factors (time t-1, time t)
   */
  def newQ(sStats: IndexedSeq[TransitionStat]): Double

  /**
   * @param sStats Sufficient statistics, marginals of prior factors
   */
  def newPi(sStats: IndexedSeq[PriorStat]): Double

  /**
   * @param sStats Sufficient statistics, marginals of prior factors
   */
  def newV(sStats: IndexedSeq[PriorStat]): Double
}