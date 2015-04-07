package dk.bayes.dsl.variable.gaussian.multivariate

import dk.bayes.math.linear.Matrix
import dk.bayes.dsl.Variable
import dk.bayes.dsl.InferEngine
import dk.bayes.dsl.variable.Gaussian

/**
 * N(m,v)
 *
 * @author Daniel Korzekwa
 */
case class MultivariateGaussian(val m: Matrix, val v: Matrix) extends Gaussian with MultivariateGaussianFactor {

  require(v.numRows() == v.numCols(), "Multivariate Gaussian covariance must be a quare matrix")
  require(m.size() == v.numRows(), "Mean size must be equal to variance square matrix size")

  def getParents(): Seq[Variable] = Nil
}

object MultivariateGaussian {

  implicit var inferEngines = Vector(
    inferMultivariateGaussianSingleNode,
    inferMultivariateGaussianSimplest,
    inferMultivariateGaussianEPNaiveBayes)
}
