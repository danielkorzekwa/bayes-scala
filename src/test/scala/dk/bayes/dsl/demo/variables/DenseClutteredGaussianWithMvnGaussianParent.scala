package dk.bayes.dsl.demo.variables

import scala.reflect._
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import dk.bayes.dsl.Variable
import dk.bayes.dsl.factor.DoubleFactor
import dk.bayes.dsl.variable.Gaussian
import dk.bayes.dsl.variable.gaussian.multivariate.MultivariateGaussian
import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian

/**
 * cluttered_gaussian ~ (1-w)*N(v.m,1) + w*N(0,a)
 */
case class DenseClutteredGaussianWithMvnGaussianParent(x: MultivariateGaussian, xIndex: Int, w: Double, a: Double, value: Double) extends Variable
    with ClutteredGaussianMvnParentFactorDense {
  def getParents(): Seq[Variable] = Vector(x)

  def getVar() = this
}

trait ClutteredGaussianMvnParentFactorDense extends DoubleFactor[DenseCanonicalGaussian, Any] {

  def getVar(): DenseClutteredGaussianWithMvnGaussianParent

  val xIndex: Int
  val w: Double
  val a: Double
  val value: Double

  val initFactorMsgUp: DenseCanonicalGaussian = DenseCanonicalGaussian(DenseVector.zeros[Double](getVar.x.m.size), DenseMatrix.eye[Double](getVar.x.m.size) * 100d)

  def calcYFactorMsgUp(x: DenseCanonicalGaussian, oldFactorMsgUp: DenseCanonicalGaussian): Option[DenseCanonicalGaussian] = {

    val oldfVarMsgUp = new DenseCanonicalGaussian(DenseMatrix(oldFactorMsgUp.k(xIndex, xIndex)), DenseVector(oldFactorMsgUp.h(xIndex)), oldFactorMsgUp.g)
    val fFactorMsgDown = (x.marginal(xIndex) / (oldfVarMsgUp)).toGaussian

    val projValue = project(fFactorMsgDown, w, a, value)
    val clutterFactorMsgUp = DenseCanonicalGaussian(projValue.m, projValue.v) / DenseCanonicalGaussian(fFactorMsgDown.m, fFactorMsgDown.v)

    val A = DenseMatrix.zeros[Double](x.h.size, 1).t
    A(0, xIndex) = 1d
    val fFactor = DenseCanonicalGaussian(A, b = DenseVector(0.0), v = DenseMatrix(1e-9))
    val newFactorMsgUp = (clutterFactorMsgUp.extend(x.h.size + 1, x.h.size) * fFactor).marginalise(x.h.size)

    Some(newFactorMsgUp)
  }

}

