package dk.bayes.math.linear

import breeze.linalg.DenseMatrix
import breeze.linalg.inv


object invchol {
  
  /**
   * @param R, where R'*R= A, cholesky decomposition
   */
  def apply(R:DenseMatrix[Double]):DenseMatrix[Double] = {
     val Rinv = inv(R)
     Rinv*Rinv.t
    
  }
}