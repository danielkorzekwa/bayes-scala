package dk.bayes.dsl.variable.gaussian.multivariatelinear

import dk.bayes.dsl.InferEngine
import dk.bayes.dsl.variable.gaussian.multivariate.MultivariateGaussian

object inferMultivariateLinearGaussianSimplest extends InferEngine[MultivariateLinearGaussian, MultivariateGaussian] {

  /**
   * Supported model: x -> y
   * x - MultivariateGaussian
   */
  def isSupported(v: MultivariateLinearGaussian): Boolean = {

    v.x.getChildren match {
      case Seq(v) => //do nothing
      case _ => return false
    }

    v.yValue.isEmpty &&
      !v.x.hasParents &&
      !v.hasChildren
  }

  def infer(v: MultivariateLinearGaussian): MultivariateGaussian = {

    val x = v.x

    val skillMean = v.a * x.m + v.b
    val skillVar = v.v + v.a * x.v * v.a.t

    new MultivariateGaussian(skillMean, skillVar)
  }
}