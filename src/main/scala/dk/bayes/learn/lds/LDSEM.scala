package dk.bayes.learn.lds

import dk.bayes.math.gaussian.Gaussian

trait LDSEM {

  /**
   * Learn parameters (prior mean and variance and emission variance) of linear dynamical systems with EM algorithm. Learning transition variance is not supported.
   *
   * @param data Sequence of sequences of observations (transition variance is zero)
   * @param priorMean Initial prior mean parameters (mean and variance)
   * @param emissionVar Initial emission variance
   */
  def learn(data: Array[Array[Double]], priorMean: Gaussian, emissionVar: Double, iterNum: Int): EMSummary
}