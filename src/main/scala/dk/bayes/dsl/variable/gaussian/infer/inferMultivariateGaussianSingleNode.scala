package dk.bayes.dsl.variable.gaussian.infer

import dk.bayes.dsl.InferEngine
import dk.bayes.dsl.variable.gaussian.MultivariateGaussian
import dk.bayes.dsl.variable.gaussian.MultivariateLinearGaussian

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