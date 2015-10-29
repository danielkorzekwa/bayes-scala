package dk.bayes.math.linear

import breeze.linalg.DenseMatrix

object hasUncountable {
  
  def apply(m:DenseMatrix[Double]):Boolean = m.valuesIterator.exists(x => x.isNaN || x.isInfinity)
}