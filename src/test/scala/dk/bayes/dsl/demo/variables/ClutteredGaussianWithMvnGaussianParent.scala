package dk.bayes.dsl.demo.variables

import dk.bayes.dsl.variable.gaussian.UnivariateGaussian
import dk.bayes.dsl.Variable
import dk.bayes.dsl.factor.DoubleFactor
import dk.bayes.math.gaussian.LinearGaussian
import dk.bayes.math.gaussian.Proj
import dk.bayes.dsl.variable.Gaussian
import dk.bayes.dsl.variable.gaussian.MultivariateGaussian
import dk.bayes.math.linear.Matrix
import dk.bayes.math.gaussian.CanonicalGaussian

/**
 * cluttered_gaussian ~ (1-w)*N(v.m,1) + w*N(0,a)
 */
case class ClutteredGaussianWithMvnGaussianParent(x: MultivariateGaussian, xIndex: Int, w: Double, a: Double, value: Double) extends Variable with ClutteredGaussianMvnParentFactor {
  def getParents(): Seq[Variable] = Vector(x)

}

trait ClutteredGaussianMvnParentFactor extends DoubleFactor[CanonicalGaussian, Any] {
  val xIndex: Int
  val w: Double
  val a: Double
  val value: Double

  def calcYFactorMsgUp(x: CanonicalGaussian, oldFactorMsgUp: CanonicalGaussian): Option[CanonicalGaussian] = {
    
    val yVarMsgUp = new CanonicalGaussian(Matrix(oldFactorMsgUp.k(xIndex, xIndex)), Matrix(oldFactorMsgUp.h(xIndex)), oldFactorMsgUp.g)
    val fFactorMsgDown = (x.marginal(xIndex) / (yVarMsgUp)).toGaussian

    val projValue = project(fFactorMsgDown, w, a, value)
    val clutterFactorMsgUp = CanonicalGaussian(projValue.m, projValue.v) / CanonicalGaussian(fFactorMsgDown.m, fFactorMsgDown.v)

    val A = Matrix.zeros(x.h.size, 1).t
    A.set(0, xIndex, 1)
    val fFactor = CanonicalGaussian(A, b = Matrix(0.0), v = Matrix(1e-9))
    val newFactorMsgUp = (clutterFactorMsgUp.extend(x.h.size + 1, x.h.size) * fFactor).marginalise(x.h.size)

   Some(newFactorMsgUp)
  }

}