package dk.bayes.factorgraph2.factor

import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian
import dk.bayes.math.gaussian.canonical.CanonicalGaussian
import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import dk.bayes.factorgraph2.variable.CanonicalGaussianVariable
import dk.bayes.factorgraph2.api.Variable
import dk.bayes.factorgraph2.api.SingleFactor

case class CanonicalGaussianFactor(v1: CanonicalGaussianVariable, m: DenseVector[Double], v: DenseMatrix[Double]) extends SingleFactor[CanonicalGaussian] {

  private var _m = m
  private var _v = v

  def getV1(): Variable[CanonicalGaussian] = v1

  def getInitialMsgV1(): CanonicalGaussian = DenseCanonicalGaussian(m, v)

  def calcNewMsgV1(): CanonicalGaussian = DenseCanonicalGaussian(_m, _v)

  def updateMeanAndVariance(newM: DenseVector[Double], newV: DenseMatrix[Double]) = {
    _m = newM
    _v = newV
  }
}