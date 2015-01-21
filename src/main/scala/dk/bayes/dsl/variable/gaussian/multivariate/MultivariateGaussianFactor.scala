package dk.bayes.dsl.variable.gaussian.multivariate

import dk.bayes.dsl.factor.SingleFactor
import dk.bayes.math.gaussian.canonical.CanonicalGaussian
import dk.bayes.math.gaussian.Gaussian
import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian

trait MultivariateGaussianFactor extends SingleFactor[DenseCanonicalGaussian] {

  this: MultivariateGaussian =>

  val factorMsgDown: DenseCanonicalGaussian = DenseCanonicalGaussian(this.m, this.v)
}