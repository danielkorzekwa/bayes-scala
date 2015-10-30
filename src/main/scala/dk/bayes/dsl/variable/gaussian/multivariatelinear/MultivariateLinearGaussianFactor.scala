package dk.bayes.dsl.variable.gaussian.multivariatelinear

import dk.bayes.dsl.factor.DoubleFactor
import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian
import breeze.linalg.DenseVector
import breeze.linalg.DenseMatrix

trait MultivariateLinearGaussianFactor extends DoubleFactor[DenseCanonicalGaussian, Any] {

  this: MultivariateLinearGaussian =>

  val initFactorMsgUp: DenseCanonicalGaussian = {
    val m = DenseVector.zeros[Double](this.b.size)
    val v = DenseMatrix.eye[Double](this.b.size) * 1000d
    DenseCanonicalGaussian(m, v)
  }

  def calcYFactorMsgUp(x: DenseCanonicalGaussian, oldFactorMsgUp: DenseCanonicalGaussian): Option[DenseCanonicalGaussian] = {
    throw new UnsupportedOperationException("Not implemented yet")
  }
}