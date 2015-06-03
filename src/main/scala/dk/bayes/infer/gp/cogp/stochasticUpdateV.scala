package dk.bayes.infer.gp.cogp

import breeze.linalg.DenseVector
import breeze.linalg.DenseMatrix

/**
 * Stochastic update for the parameters (mu,S) of p(v|y)
 *
 * Nguyen et al. Collaborative Multi-output Gaussian Processes, 2014
 */

object stochasticUpdateV {

  def apply():(DenseVector[Double],DenseMatrix[Double]) = {
   (DenseVector(0d),DenseMatrix(0d))
  }
}