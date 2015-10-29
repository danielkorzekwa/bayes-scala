package dk.bayes.math.linear

import breeze.linalg.DenseMatrix
import breeze.numerics._
import breeze.linalg.DenseVector

object isIdentical {

  def apply(x1: DenseMatrix[Double], x2: DenseMatrix[Double], tol: Double): Boolean = !abs(x1 - x2).valuesIterator.exists { x => x > tol }
  
   def apply(x1: DenseVector[Double], x2: DenseVector[Double], tol: Double): Boolean = !abs(x1 - x2).valuesIterator.exists { x => x > tol }
}