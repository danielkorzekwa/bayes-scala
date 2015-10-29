package dk.bayes.dsl.variable.gaussian.multivariate

import scala.Vector

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import dk.bayes.dsl.Variable
import dk.bayes.dsl.variable.Gaussian

/**
 * N(m,v)
 *
 * @author Daniel Korzekwa
 */
case class MultivariateGaussian(val m: DenseVector[Double], val v: DenseMatrix[Double]) extends Gaussian with MultivariateGaussianFactor {

  require(v.rows == v.cols, "Multivariate Gaussian covariance must be a quare matrix")
  require(m.size == v.rows, "Mean size must be equal to variance square matrix size")

  def getParents(): Seq[Variable] = Nil
}

object MultivariateGaussian {

  implicit var inferEngines = Vector(
    inferMultivariateGaussianSingleNode,
    inferMultivariateGaussianSimplest,
    inferMultivariateGaussianEPNaiveBayes)
}
