package dk.bayes.factorgraph2.factor

import breeze.linalg.CSCMatrix
import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import breeze.linalg.SparseVector
import dk.bayes.factorgraph2.api.DoubleFactor
import dk.bayes.factorgraph2.api.Variable
import dk.bayes.factorgraph2.variable.BernVariable
import dk.bayes.factorgraph2.variable.CanonicalGaussianVariable
import dk.bayes.math.gaussian.Gaussian
import dk.bayes.math.gaussian.canonical.CanonicalGaussian
import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian
import dk.bayes.math.gaussian.canonical.SparseCanonicalGaussian

case class StepFunctionFactor(v1: CanonicalGaussianVariable, v2: BernVariable, v1Size: Int, v1Index: Int, v: Double) extends DoubleFactor[CanonicalGaussian, Double] {

  def getV1(): Variable[CanonicalGaussian] = v1
  def getV2(): Variable[Double] = v2

  def getInitialMsgV1(): CanonicalGaussian = createZeroFactorMsgUp(v1Size, Double.NaN)

  def calcNewMsgV1(): CanonicalGaussian = {

    val v1 = this.getV1.get.asInstanceOf[DenseCanonicalGaussian]
    val msgV1 = this.getMsgV1.get.asInstanceOf[SparseCanonicalGaussian]
    val exceeds = v2.k==1

    val pointVarMsgUp = new DenseCanonicalGaussian(DenseMatrix(msgV1.k(v1Index, v1Index)), DenseVector(msgV1.h(v1Index)), msgV1.g)
    val pointFactorMsgDown = (v1.marginal(v1Index) / (pointVarMsgUp)).toGaussian + Gaussian(0, v)

    //compute new point factor msg up
    val projValue = (pointFactorMsgDown).truncate(0, exceeds)

    val pointVarMsgUpNew = (projValue / pointFactorMsgDown) + Gaussian(0, v)
    val pointVarMsgUpNewCanon = DenseCanonicalGaussian(pointVarMsgUpNew.m, pointVarMsgUpNew.v)

    val msgV1New = createZeroFactorMsgUp(v1Size, pointVarMsgUpNewCanon.g)
    msgV1New.k(v1Index, v1Index) = pointVarMsgUpNewCanon.k(0, 0)
    msgV1New.h(v1Index) = pointVarMsgUpNewCanon.h(0)
    msgV1New

  }

  def getInitialMsgV2(): Double = Double.NaN

  def calcNewMsgV2(): Double = ???

  private def createZeroFactorMsgUp(n: Int, g: Double): SparseCanonicalGaussian = {
    val newFactorMsgUpK = CSCMatrix.zeros[Double](n, n)
    val newFactorMsgUpH = SparseVector.zeros[Double](n)
    val newFactorMsgUp = new SparseCanonicalGaussian(newFactorMsgUpK, newFactorMsgUpH, g)

    newFactorMsgUp
  }
}

