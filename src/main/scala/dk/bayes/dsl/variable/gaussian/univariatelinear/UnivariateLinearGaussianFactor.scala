package dk.bayes.dsl.variable.gaussian.univariatelinear

import dk.bayes.dsl.factor.DoubleFactor
import dk.bayes.math.gaussian.canonical.CanonicalGaussian
import dk.bayes.math.gaussian.Gaussian
import scala.reflect._
import scala.reflect.runtime.universe._
import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian

trait UnivariateLinearGaussianFactor extends DoubleFactor[Gaussian, Any] {
  this: UnivariateLinearGaussian =>

  val initFactorMsgUp: Gaussian = new Gaussian(0, Double.PositiveInfinity)

  def calcYFactorMsgUp(x: Gaussian, oldFactorMsgUp: Gaussian): Option[Gaussian] = {
    require(this.yValue.isDefined, "Not supported")

    val xVarMsgDown = Gaussian(x.m, x.v) / Gaussian(oldFactorMsgUp.m, oldFactorMsgUp.v)

    val xCanon = DenseCanonicalGaussian(xVarMsgDown.m, xVarMsgDown.v)
    val yCanon = DenseCanonicalGaussian(a = this.a, this.b, this.v)
    val xPosterior = (xCanon.extend(2, 0) * yCanon).withEvidence(1, this.yValue.get).toGaussian

    val newFactorMsgUp = xPosterior / xVarMsgDown

    Some(newFactorMsgUp)
  }

}