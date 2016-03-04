package dk.bayes.math.accuracy

import breeze.linalg.DenseMatrix
import breeze.linalg._
import breeze.numerics._
import breeze.stats._

object aggregate {

  /**
   * @param data [x,y]
   *
   * @return [avg x by bin, avg y by corresponding x bin]
   */
  def apply(data: DenseMatrix[Double], binNum: Int = 10): DenseMatrix[Double] = {
    require(binNum > 0, "Number of bins must be bigger than 0")

    val x = data(::, 0)
    val binSize = ceil(x.size.toDouble / binNum).toInt

    val aggrVectors = (0 until x.size/binSize).map { i =>

      val x = data(i * binSize until i * binSize + binSize, 0)
      val y = data(i * binSize until i * binSize + binSize, 1)

      DenseVector(mean(x), mean(y))

    }
    DenseVector.horzcat(aggrVectors: _*).t
  }
}