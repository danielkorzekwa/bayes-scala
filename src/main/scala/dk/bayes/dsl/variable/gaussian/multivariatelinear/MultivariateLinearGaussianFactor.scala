package dk.bayes.dsl.variable.gaussian.multivariatelinear

import dk.bayes.dsl.factor.DoubleFactor
import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian
import breeze.linalg.DenseVector
import breeze.linalg.DenseMatrix
import dk.bayes.dsl.variable.Gaussian
import dk.bayes.dsl.infer
import dk.bayes.dsl.variable.gaussian.multivariate.MultivariateGaussian

trait MultivariateLinearGaussianFactor extends DoubleFactor[DenseCanonicalGaussian, Any] {

  this: MultivariateLinearGaussian =>

  val initFactorMsgUp: DenseCanonicalGaussian = {
    val m = DenseVector.zeros[Double](this.b.size)
    val v = DenseMatrix.eye[Double](this.b.size) * 1000d
    DenseCanonicalGaussian(m, v)
  }

  def calcYFactorMsgUp(x: DenseCanonicalGaussian, oldFactorMsgUp: DenseCanonicalGaussian): Option[DenseCanonicalGaussian] = {

    val xVarMsgDown = x / oldFactorMsgUp

    val xVariable = Gaussian(xVarMsgDown.mean, xVarMsgDown.variance)
    val yVariable = Gaussian(this.a, xVariable, this.b, this.v, this.yValue.get)

    val xPosterior: MultivariateGaussian = infer(xVariable)

    val newFactorMsgUp = DenseCanonicalGaussian(xPosterior.m, xPosterior.v) / xVarMsgDown
    Some(newFactorMsgUp)
  }
}