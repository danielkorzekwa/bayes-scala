package dk.bayes.dsl.variable.categorical.infer

import dk.bayes.dsl.variable.categorical.MvnGaussianThreshold
import dk.bayes.dsl.InferEngine
import dk.bayes.dsl.variable.Gaussian
import dk.bayes.dsl.variable.gaussian.multivariate.MultivariateGaussian
import scala.math._
import dk.bayes.dsl.variable.gaussian.univariate.UnivariateGaussian

object inferEngineMvnGaussianThreshold extends InferEngine[MvnGaussianThreshold, UnivariateGaussian] {

  /**
   * Supported model:
   *
   * x -> y, where
   *
   * x ~ MultivariateGaussian
   * y ~  MvnGaussianThreshold
   *
   */
  def isSupported(y: MvnGaussianThreshold): Boolean = {
    y.getChildren().isEmpty &&
      y.getParents().size == 1 && y.getParents()(0).isInstanceOf[MultivariateGaussian] &&
      y.getParents().size == 1 && y.getParents()(0).getChildren().size == 1
  }

  def infer(y: MvnGaussianThreshold): UnivariateGaussian = {

    val x = y.getParents()(0).asInstanceOf[MultivariateGaussian]

    val xAtIndex = dk.bayes.math.gaussian.Gaussian(x.m(y.xIndex), x.v(y.xIndex, y.xIndex))

    val exceedsProb = 1 - xAtIndex.copy(v = xAtIndex.v + y.v).cdf(0)

    val exeedsProb1Sigma = 1 - xAtIndex.copy(m = xAtIndex.m + sqrt(xAtIndex.v), v = xAtIndex.v + y.v).cdf(0)

    Gaussian(exceedsProb, pow(exeedsProb1Sigma - exceedsProb, 2))
  }
}