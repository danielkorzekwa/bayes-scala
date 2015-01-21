package dk.bayes.dsl.variable.gaussian.univariate

import dk.bayes.dsl.factor.SingleFactor
import dk.bayes.math.gaussian.Gaussian
import dk.bayes.dsl.variable.gaussian._

trait UnivariateGaussianFactor extends SingleFactor[Gaussian] {

  this: UnivariateGaussian =>

  val factorMsgDown: Gaussian = Gaussian(this.m, this.v)
}