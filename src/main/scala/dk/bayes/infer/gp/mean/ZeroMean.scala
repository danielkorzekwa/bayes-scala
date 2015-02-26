package dk.bayes.infer.gp.mean

import dk.bayes.math.linear.Matrix

case class ZeroMean() extends MeanFunc {

  def mean(x: Matrix): Matrix = Matrix.zeros(x.numRows, 1)
}