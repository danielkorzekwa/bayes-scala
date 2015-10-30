package dk.bayes.dsl.variable.gaussian.multivariate

import dk.bayes.dsl.InferEngine
import dk.bayes.dsl.variable.gaussian.multivariatelinear.MultivariateLinearGaussian
import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian
import breeze.linalg.inv
import breeze.linalg.cholesky
import dk.bayes.math.linear.invchol

object inferMultivariateGaussianSimplest extends InferEngine[MultivariateGaussian, MultivariateGaussian] {

  /**
   * Supported model: x -> z
   * z - MultivariateLinearGaussian z = x + gaussian noise
   */
  def isSupported(x: MultivariateGaussian): Boolean = {

    val child = x.getChildren match {
      case Seq(child) if child.isInstanceOf[MultivariateLinearGaussian] => child.asInstanceOf[MultivariateLinearGaussian]
      case _ => return false
    }

    (child.getParents().size == 1 && child.getParents()(0).eq(x)) &&
      !child.hasChildren &&
      child.yValue.isDefined

  }

  def infer(x: MultivariateGaussian): MultivariateGaussian = {
    val child = x.getChildren.head.asInstanceOf[MultivariateLinearGaussian]

    val xVInv = invchol(cholesky(x.v).t)
    val childVInv = invchol(cholesky(child.v).t)

    val posteriorVar = invchol(cholesky(xVInv + child.a.t * childVInv * child.a).t)
    val posteriorMean = posteriorVar * (child.a.t * childVInv * (child.yValue.get - child.b) + xVInv * x.m)

    new MultivariateGaussian(posteriorMean, posteriorVar)
  }
}