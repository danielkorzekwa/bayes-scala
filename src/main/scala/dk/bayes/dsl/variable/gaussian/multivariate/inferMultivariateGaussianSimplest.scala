package dk.bayes.dsl.variable.gaussian.multivariate

import dk.bayes.dsl.InferEngine
import dk.bayes.dsl.variable.gaussian.multivariatelinear.MultivariateLinearGaussian

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
      child.b.size == x.m.size &&
      child.b.toArray.sum == 0 &&
      child.yValue.isDefined &&
      x.m.toArray.sum == 0
  }

  def infer(x: MultivariateGaussian): MultivariateGaussian = {
    val child = x.getChildren.head.asInstanceOf[MultivariateLinearGaussian]

    val posteriorVar = (x.v.inv + child.a.t * child.v.inv * child.a).inv
    val posteriorMean = posteriorVar * (child.a.t * child.v.inv * child.yValue.get)
    new MultivariateGaussian(posteriorMean, posteriorVar)
  }
}