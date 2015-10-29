package dk.bayes.math.linear

import breeze.linalg.DenseMatrix

object filterNot {
  
  def apply(x:DenseMatrix[Double],rowIndex: Int, columnIndex: Int):DenseMatrix[Double] = {
    val m1 = filterNotRow(x,rowIndex)
    val m2 = filterNotColumn(m1,columnIndex)
    m2
  }
}