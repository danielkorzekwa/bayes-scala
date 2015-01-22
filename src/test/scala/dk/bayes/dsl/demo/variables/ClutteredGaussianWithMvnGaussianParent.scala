package dk.bayes.dsl.demo.variables

import dk.bayes.dsl.Variable
import dk.bayes.dsl.factor.DoubleFactor
import dk.bayes.math.gaussian.LinearGaussian
import dk.bayes.math.gaussian.Proj
import dk.bayes.dsl.variable.Gaussian
import dk.bayes.math.linear.Matrix
import dk.bayes.math.gaussian.canonical.CanonicalGaussian
import scala.reflect.runtime.universe._
import scala.reflect.ClassTag
import scala.reflect._
import dk.bayes.dsl.variable.gaussian.multivariate.MultivariateGaussian
import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian
import dk.bayes.math.gaussian.canonical.SparseCanonicalGaussian
import breeze.linalg.CSCMatrix
import breeze.linalg.DenseVector
import breeze.linalg.SparseVector
import dk.bayes.math.gaussian.canonical.SparseCanonicalGaussian
import dk.bayes.math.gaussian.canonical.SparseCanonicalGaussian

/**
 * cluttered_gaussian ~ (1-w)*N(v.m,1) + w*N(0,a)
 */
case class ClutteredGaussianWithMvnGaussianParent(x: MultivariateGaussian, xIndex: Int, w: Double, a: Double, value: Double) extends Variable
  with ClutteredGaussianMvnParentFactor {
  def getParents(): Seq[Variable] = Vector(x)

  def getVar() = this
}

trait ClutteredGaussianMvnParentFactor extends DoubleFactor[CanonicalGaussian, Any] {

  def getVar(): ClutteredGaussianWithMvnGaussianParent

  val xIndex: Int
  val w: Double
  val a: Double
  val value: Double

  val initFactorMsgUp: SparseCanonicalGaussian = createZeroFactorMsgUp(getVar.x.m.size, Double.NaN)

  def calcYFactorMsgUp(x: CanonicalGaussian, oldFactorMsgUp: CanonicalGaussian): Option[CanonicalGaussian] = {
    val xInternal = x.asInstanceOf[DenseCanonicalGaussian]
    val oldFactorMsgUpInternal = oldFactorMsgUp.asInstanceOf[SparseCanonicalGaussian]
    calcYFactorMsgUpInternal(xInternal, oldFactorMsgUpInternal)
  }

  private def calcYFactorMsgUpInternal(x: DenseCanonicalGaussian, oldFactorMsgUp: SparseCanonicalGaussian): Option[SparseCanonicalGaussian] = {

    val oldfVarMsgUp = new DenseCanonicalGaussian(Matrix(oldFactorMsgUp.k(xIndex, xIndex)), Matrix(oldFactorMsgUp.h(xIndex)), oldFactorMsgUp.g)
    val fFactorMsgDown = (x.marginal(xIndex) / (oldfVarMsgUp)).toGaussian

    val projValue = project(fFactorMsgDown, w, a, value)
    val clutterFactorMsgUp = DenseCanonicalGaussian(projValue.m, projValue.v) / DenseCanonicalGaussian(fFactorMsgDown.m, fFactorMsgDown.v)

    val newFactorMsgUp = createZeroFactorMsgUp(getVar.x.m.size, clutterFactorMsgUp.g)
    newFactorMsgUp.k(xIndex, xIndex) = clutterFactorMsgUp.k(0, 0)
    newFactorMsgUp.h(xIndex) = clutterFactorMsgUp.h(0)

    Some(newFactorMsgUp)
  }

  private def createZeroFactorMsgUp(n: Int, g: Double): SparseCanonicalGaussian = {
    val newFactorMsgUpK = CSCMatrix.zeros[Double](n, n)
    val newFactorMsgUpH = SparseVector.zeros[Double](n)
    val newFactorMsgUp = new SparseCanonicalGaussian(newFactorMsgUpK, newFactorMsgUpH, g)

    newFactorMsgUp
  }

}