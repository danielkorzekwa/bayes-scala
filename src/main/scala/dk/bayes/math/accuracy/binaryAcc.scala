package dk.bayes.math.accuracy

import breeze.linalg.DenseVector

object binaryAcc {

  def apply(expected: DenseVector[Double], actual: DenseVector[Double]): Double = {
    require(expected.size == actual.size)

    //true positive + true negative
    val tptn = expected.toArray.zip(actual.toArray).map {
      case (expected, actual) =>

        val predictedBinary = if (expected > 0.5) 1 else 0
        if (predictedBinary == actual) 1d else 0
    }
    tptn.sum / expected.size
  }
}