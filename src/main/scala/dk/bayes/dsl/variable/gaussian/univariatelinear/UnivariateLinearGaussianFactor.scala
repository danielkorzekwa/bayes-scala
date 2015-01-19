package dk.bayes.dsl.variable.gaussian.univariatelinear

import dk.bayes.dsl.factor.DoubleFactor
import dk.bayes.math.gaussian.CanonicalGaussian
import dk.bayes.math.gaussian.Gaussian
import scala.reflect._
import scala.reflect.runtime.universe._

trait UnivariateLinearGaussianFactor extends DoubleFactor[Gaussian, Any] {
  // val msgUpType = typeTag[UnivariateGaussian]
  def getVar(): UnivariateLinearGaussian

  val initFactorMsgUp: Gaussian = new Gaussian(0, Double.PositiveInfinity)

  def calcYFactorMsgUp(x: Gaussian, oldFactorMsgUp: Gaussian): Option[Gaussian] = {
    require(getVar.yValue.isDefined, "Not supported")

    val xVarMsgDown = Gaussian(x.m, x.v) / Gaussian(oldFactorMsgUp.m, oldFactorMsgUp.v)

    val xCanon = CanonicalGaussian(xVarMsgDown.m, xVarMsgDown.v)
    val yCanon = CanonicalGaussian(a = getVar.a, getVar.b, getVar.v)
    val xPosterior = (xCanon.extend(2, 0) * yCanon).withEvidence(1, getVar.yValue.get).toGaussian

    val newFactorMsgUp = xPosterior / xVarMsgDown

    Some(newFactorMsgUp)
  }

}