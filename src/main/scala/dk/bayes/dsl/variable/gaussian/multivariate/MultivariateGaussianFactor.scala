package dk.bayes.dsl.variable.gaussian.multivariate

import dk.bayes.dsl.factor.SingleFactor
import dk.bayes.math.gaussian.CanonicalGaussian

 trait MultivariateGaussianFactor extends SingleFactor[CanonicalGaussian] {

  def getThis():MultivariateGaussian
  
  val factorMsgDown: CanonicalGaussian = CanonicalGaussian(getThis().m,getThis().v)
}