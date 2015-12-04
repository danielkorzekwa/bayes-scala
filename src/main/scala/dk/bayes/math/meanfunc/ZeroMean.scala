package dk.bayes.math.meanfunc

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector

case class ZeroMean() extends MeanFunc {

  def mean(x: DenseMatrix[Double]): DenseVector[Double] = DenseVector.zeros[Double](x.rows)
}