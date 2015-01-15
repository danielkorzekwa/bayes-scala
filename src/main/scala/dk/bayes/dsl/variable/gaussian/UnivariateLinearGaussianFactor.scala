package dk.bayes.dsl.variable.gaussian

import dk.bayes.dsl.factor.DoubleFactor
import dk.bayes.math.gaussian.CanonicalGaussian
import dk.bayes.math.gaussian.Gaussian

trait UnivariateLinearGaussianFactor extends DoubleFactor[UnivariateGaussian, Any] {

  val variable: UnivariateLinearGaussian

  def calcYFactorMsgUp(x: UnivariateGaussian, oldFactorMsgUp: UnivariateGaussian): Option[UnivariateGaussian] = {
    require(variable.yValue.isDefined, "Not supported")

    val xVarMsgDown = Gaussian(x.m, x.v) / Gaussian(oldFactorMsgUp.m, oldFactorMsgUp.v)

    val xCanon = CanonicalGaussian(xVarMsgDown.m, xVarMsgDown.v)
    val yCanon = CanonicalGaussian(a = variable.a, variable.b, variable.v)
    val xPosterior = (xCanon.extend(2, 0) * yCanon).withEvidence(1, variable.yValue.get).toGaussian

    val newFactorMsgUp = xPosterior / xVarMsgDown

    Some(UnivariateGaussian(newFactorMsgUp.m, newFactorMsgUp.v))
  }

}