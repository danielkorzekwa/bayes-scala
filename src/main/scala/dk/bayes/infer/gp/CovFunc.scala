package dk.bayes.infer.gp

import dk.bayes.math.gaussian.Linear._

/**
 * Covariance function that measures similarity between two points in some input space.
 * 
 */
trait CovFunc {

  /**
   * Returns similarity between two vectors.
   *
   * @param x1 [Dx1] vector
   * @param x2 [Dx1] vector
   */
  def cov(x1:Matrix,x2:Matrix):Double
}