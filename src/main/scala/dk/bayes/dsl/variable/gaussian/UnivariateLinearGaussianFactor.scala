package dk.bayes.dsl.variable.gaussian

import dk.bayes.dsl.factor.DoubleFactor
import dk.bayes.math.gaussian.CanonicalGaussian

trait UnivariateLinearGaussianFactor extends DoubleFactor[UnivariateGaussian, Any] {

  val variable: UnivariateLinearGaussian
  
  def marginals(x: Option[UnivariateGaussian], y: Option[Any]): (Option[UnivariateGaussian], Option[Any]) = {
    
    require(x.isDefined,"Not supported")
    require(y.isEmpty,"Not supported")
    require(variable.yValue.isDefined,"Not supported")
    
    val xCanon = CanonicalGaussian(x.get.m, x.get.v)
    val yCanon = CanonicalGaussian(a = variable.a, variable.b, variable.v)
    val marginal = (xCanon.extend(2, 0) * yCanon).withEvidence(1, variable.yValue.get).toGaussian

    (Some(new UnivariateGaussian(marginal.m, marginal.v)), None)

  }

}