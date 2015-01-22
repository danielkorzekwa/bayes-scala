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
case class DenseClutteredGaussianWithMvnGaussianParent(x: MultivariateGaussian, xIndex: Int, w: Double, a: Double, value: Double) extends Variable
with ClutteredGaussianMvnParentFactorDense
   {
  def getParents(): Seq[Variable] = Vector(x)

  def getVar() = this
}



  trait ClutteredGaussianMvnParentFactorDense extends DoubleFactor[DenseCanonicalGaussian, Any] {

    def getVar(): DenseClutteredGaussianWithMvnGaussianParent

    val xIndex: Int
    val w: Double
    val a: Double
    val value: Double

    val initFactorMsgUp: DenseCanonicalGaussian = DenseCanonicalGaussian(Matrix.zeros(getVar.x.m.size, 1), Matrix.identity(getVar.x.m.size) * Double.PositiveInfinity)

    def calcYFactorMsgUp(x: DenseCanonicalGaussian, oldFactorMsgUp: DenseCanonicalGaussian): Option[DenseCanonicalGaussian] = {

      val oldfVarMsgUp = new DenseCanonicalGaussian(Matrix(oldFactorMsgUp.k(xIndex, xIndex)), Matrix(oldFactorMsgUp.h(xIndex)), oldFactorMsgUp.g)
      val fFactorMsgDown = (x.marginal(xIndex) / (oldfVarMsgUp)).toGaussian

      val projValue = project(fFactorMsgDown, w, a, value)
      val clutterFactorMsgUp = DenseCanonicalGaussian(projValue.m, projValue.v) / DenseCanonicalGaussian(fFactorMsgDown.m, fFactorMsgDown.v)

      val A = Matrix.zeros(x.h.size, 1).t
      A.set(0, xIndex, 1)
      val fFactor = DenseCanonicalGaussian(A, b = Matrix(0.0), v = Matrix(1e-9))
      val newFactorMsgUp = (clutterFactorMsgUp.extend(x.h.size + 1, x.h.size) * fFactor).marginalise(x.h.size)

      Some(newFactorMsgUp)
    }

  }

