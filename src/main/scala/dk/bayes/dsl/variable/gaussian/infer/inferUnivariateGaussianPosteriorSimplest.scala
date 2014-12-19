package dk.bayes.dsl.variable.gaussian.infer

import dk.bayes.dsl.InferEngine
import dk.bayes.dsl.variable.gaussian.UnivariateGaussian
import dk.bayes.dsl.variable.gaussian.UnivariateLinearGaussian

object inferUnivariateGaussianPosteriorSimplest extends InferEngine[UnivariateGaussian, UnivariateGaussian] {

  def isSupported(x: UnivariateGaussian): Boolean = {

    /**
     * Supported model: x -> z
     * z - UnivariateLinearGaussian z = x + gaussian noise
     */
    val child = x.getChildren match {
      case Seq(child) if child.isInstanceOf[UnivariateLinearGaussian] => child.asInstanceOf[UnivariateLinearGaussian]
      case _ => return false
    }

    (child.getParents().size == 1 && child.getParents()(0).eq(x)) &&
      !child.hasChildren &&
      child.a.size == 1 &&
      child.yValue.isDefined

  }

  def infer(x: UnivariateGaussian): UnivariateGaussian = {

    val child = x.getChildren.head.asInstanceOf[UnivariateLinearGaussian]

    val posteriorVar = 1d / (1d / x.v + 1d / child.v)
    val posteriorMean = posteriorVar * ((1d / child.v) * child.yValue.get + (1d / x.v) * x.m)
    new UnivariateGaussian(posteriorMean, posteriorVar)
  }
}