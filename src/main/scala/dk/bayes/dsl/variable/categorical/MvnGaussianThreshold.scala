package dk.bayes.dsl.variable.categorical

import dk.bayes.dsl.variable.gaussian.multivariate.MultivariateGaussian
import dk.bayes.dsl.Variable
import dk.bayes.dsl.factor.DoubleFactor
import dk.bayes.math.gaussian.canonical.CanonicalGaussian
import dk.bayes.math.gaussian.canonical.SparseCanonicalGaussian
import breeze.linalg.CSCMatrix
import breeze.linalg.SparseVector
import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian
import dk.bayes.math.linear.Matrix
import MvnGaussianThreshold._
import dk.bayes.math.gaussian.Gaussian
import dk.bayes.dsl.variable.categorical.infer.inferEngineMvnGaussianThreshold

/**
 * p(exceeds=true) = I(x + N(0,v)>threshold)
 */
case class MvnGaussianThreshold(x: MultivariateGaussian, xIndex: Int, val threshold: Double = 0, v: Double, exceeds: Option[Boolean] = None) extends Variable with MvnGaussianThresholdFactor {

  def getParents(): Seq[MultivariateGaussian] = Vector(x)
}

object MvnGaussianThreshold {

  /**
   * Set up the inference engine for MvnGaussianThreshold variable.
   */
  implicit val inferEngine = Vector(inferEngineMvnGaussianThreshold)

  trait MvnGaussianThresholdFactor extends DoubleFactor[CanonicalGaussian, Any] {

    this: MvnGaussianThreshold =>

    val initFactorMsgUp: SparseCanonicalGaussian = createZeroFactorMsgUp(this.x.m.size, Double.NaN)

    def calcYFactorMsgUp(x: CanonicalGaussian, oldFactorMsgUp: CanonicalGaussian): Option[CanonicalGaussian] = {
      val xInternal = x.asInstanceOf[DenseCanonicalGaussian]
      val oldFactorMsgUpInternal = oldFactorMsgUp.asInstanceOf[SparseCanonicalGaussian]
      calcYFactorMsgUpInternal(xInternal, oldFactorMsgUpInternal)
    }

    private def calcYFactorMsgUpInternal(x: DenseCanonicalGaussian, oldFactorMsgUp: SparseCanonicalGaussian): Option[SparseCanonicalGaussian] = {

      val oldYVarMsgUp = new DenseCanonicalGaussian(Matrix(oldFactorMsgUp.k(xIndex, xIndex)), Matrix(oldFactorMsgUp.h(xIndex)), oldFactorMsgUp.g)
      val factorMsgDown = (x.marginal(xIndex) / (oldYVarMsgUp)).toGaussian + Gaussian(0, v)

      //compute new factor msg up
      val projValue = (factorMsgDown).truncate(0, exceeds.get)
      val yVarMsgUp = (projValue / factorMsgDown) + Gaussian(0, v)
      val yVarMsgUpCanon = DenseCanonicalGaussian(yVarMsgUp.m, yVarMsgUp.v)

      val newFactorMsgUp = createZeroFactorMsgUp(this.x.m.size, yVarMsgUpCanon.g)
      newFactorMsgUp.k(xIndex, xIndex) = yVarMsgUpCanon.k(0, 0)
      newFactorMsgUp.h(xIndex) = yVarMsgUpCanon.h(0)

      Some(newFactorMsgUp)
    }

    private def createZeroFactorMsgUp(n: Int, g: Double): SparseCanonicalGaussian = {
      val newFactorMsgUpK = CSCMatrix.zeros[Double](n, n)
      val newFactorMsgUpH = SparseVector.zeros[Double](n)
      val newFactorMsgUp = new SparseCanonicalGaussian(newFactorMsgUpK, newFactorMsgUpH, g)

      newFactorMsgUp
    }

  }

}