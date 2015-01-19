package dk.bayes.dsl.variable.gaussian.multivariate

import dk.bayes.dsl.InferEngine
import dk.bayes.dsl.variable.gaussian.multivariatelinear.MultivariateLinearGaussian

object inferMultivariateGaussianSingleNode extends InferEngine[MultivariateGaussian, MultivariateGaussian] {

  /**
   * Supported model: x
   *
   */
  def isSupported(x: MultivariateGaussian): Boolean = {
  !x.hasParents && !x.hasChildren
  }

  def infer(x: MultivariateGaussian): MultivariateGaussian = {
   x
  }
}