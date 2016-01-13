package dk.bayes.math.accuracy

import breeze.linalg.DenseVector
import breeze.numerics._
import breeze.linalg._

object rmse {

  def apply(v1: DenseVector[Double], v2: DenseVector[Double]): Double = {
    require(v1.size == v2.size)

    sqrt(sum(pow(v1 - v2, 2)) / v1.size)
  }
}