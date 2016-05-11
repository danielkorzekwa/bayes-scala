package dk.bayes.math.accuracy

import breeze.linalg.DenseVector
import breeze.numerics._
import breeze.linalg._

object loglik {

  def apply(expected: DenseVector[Double], actual: DenseVector[Double]): Double = {
    require(expected.size == actual.size)

    val loglik = expected.toArray.zip(actual.toArray).map {
      case (expected, actual) =>
        if (actual == 1) log(expected)
        else if (actual == 0) log1p(-expected)
        else throw new IllegalArgumentException("Actual value must be 0 or 1:" + actual)
    }
    sum(loglik)
  }
  
   def apply(expected: DenseMatrix[Double], actual: DenseVector[Double]): Double = {
    require(expected.rows == actual.size)

     val loglik = actual.mapPairs{(index,actual) => 
      log(expected(index,actual.toInt))
    }
  
   sum( loglik)
  }
}