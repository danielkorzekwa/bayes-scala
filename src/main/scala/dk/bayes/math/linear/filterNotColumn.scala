package dk.bayes.math.linear

import breeze.linalg.DenseMatrix

object filterNotColumn {
  
  def apply(x:DenseMatrix[Double],columnIndex:Int):DenseMatrix[Double] = {
    
     val newMatrix = columnIndex match {
      case 0                              => x(::,1 until x.cols)
      case columnIndex if columnIndex == (x.cols - 1) => x(::,0 until x.cols-1)
      case _ => {

        val left = x(::,0 until columnIndex)
        val right = x(::,columnIndex+1 until x.cols)

        DenseMatrix.horzcat(left,right)
      }
    }
   newMatrix
    
  }
}