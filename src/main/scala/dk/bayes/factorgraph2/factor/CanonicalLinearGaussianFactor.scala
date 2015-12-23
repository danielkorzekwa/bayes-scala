package dk.bayes.factorgraph2.factor

import dk.bayes.math.gaussian.canonical.CanonicalGaussian
import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian
import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import dk.bayes.dsl._
import dk.bayes.factorgraph2.api.DoubleFactor
import dk.bayes.factorgraph2.variable.CanonicalGaussianVariable
import dk.bayes.factorgraph2.api.Variable
import breeze.linalg.cholesky
import dk.bayes.math.linear.invchol
import breeze.linalg.inv
import dk.bayes.math.gaussian.canonical.CanonicalLinearGaussianMsgFactory
import dk.bayes.math.gaussian.canonical.CanonicalLinearGaussianMsgFactory

/**
 * Linear Conditional Gaussian p(t|x) = N(t|Ax+b,v)
 */
case class CanonicalLinearGaussianFactor(v1: CanonicalGaussianVariable, v2: CanonicalGaussianVariable, a: DenseMatrix[Double], b: DenseVector[Double], v: DenseMatrix[Double]) extends DoubleFactor[CanonicalGaussian, CanonicalGaussian] {

  private val canonicalLinearGaussianMsgUpFactory = CanonicalLinearGaussianMsgFactory(a, b, v)

  def getV1(): Variable[CanonicalGaussian] = v1
  def getV2(): Variable[CanonicalGaussian] = v2

  def getInitialMsgV1(): CanonicalGaussian = {
    val m = DenseVector.zeros[Double](a.cols)
    val v = DenseMatrix.eye[Double](a.cols) * 1000d
    DenseCanonicalGaussian(m, v)
  }

  def getInitialMsgV2(): CanonicalGaussian = {
    val m = DenseVector.zeros[Double](a.rows)
    val v = DenseMatrix.eye[Double](a.rows) * 1000d
    DenseCanonicalGaussian(m, v)
  }

  def calcNewMsgV1(): CanonicalGaussian = {

    val v2 = this.getV2.get.asInstanceOf[DenseCanonicalGaussian]
    val msgV2 = this.getMsgV2.get.asInstanceOf[DenseCanonicalGaussian]
    val v2MsgUp = v2 / msgV2

    val msgV1New = canonicalLinearGaussianMsgUpFactory.msgUp(v2MsgUp)

    msgV1New
  }

  def calcNewMsgV2(): CanonicalGaussian = calcNewMsgV2(canonicalLinearGaussianMsgUpFactory)

  def calcNewMsgV2(a: DenseMatrix[Double], b: DenseVector[Double], v: DenseMatrix[Double]): CanonicalGaussian = calcNewMsgV2(CanonicalLinearGaussianMsgFactory(a, b, v))

  def calcNewMsgV2(canonicalLinearGaussianMsgUpFactory: CanonicalLinearGaussianMsgFactory): CanonicalGaussian = {
    val v1 = this.getV1.get.asInstanceOf[DenseCanonicalGaussian]
    val msgV1 = this.getMsgV1.get.asInstanceOf[DenseCanonicalGaussian]

    val v1MsgDown = v1 / msgV1

    val msgV2 = canonicalLinearGaussianMsgUpFactory.msgDown(v1MsgDown)

    msgV2
  }

}