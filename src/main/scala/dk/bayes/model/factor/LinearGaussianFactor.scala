package dk.bayes.model.factor

import dk.bayes.gaussian.CanonicalGaussian
import dk.bayes.gaussian.CanonicalGaussian
import dk.bayes.gaussian.Linear._
import dk.bayes.gaussian.CanonicalGaussianMultiply
import dk.bayes.gaussian.LinearGaussian
import dk.bayes.gaussian.Gaussian
import dk.bayes.model.factor.BivariateGaussianFactor
import dk.bayes.model.factor.GaussianFactor
import dk.bayes.model.factor.api.Factor
import dk.bayes.model.factor.api.DoubleFactor

/**
 * This class represents a factor for a Linear Gaussian Distribution. N(ax + b,v)
 *
 * @author Daniel Korzekwa
 *
 * @param parentVarId
 * @param varId
 * @param a Mean term of N(ax + b,v)
 * @param b Mean term of N(ax + b,v)
 * @param v Variance term of N(ax + b,v)
 */
case class LinearGaussianFactor(parentVarId: Int, varId: Int, a: Double, b: Double, v: Double) extends DoubleFactor {

  def getVariableIds(): Seq[Int] = Vector(parentVarId, varId)

  def marginal(varId: Int): GaussianFactor = GaussianFactor(varId, 0, Double.PositiveInfinity)

  def productMarginal(marginalVarId: Int, factor1: Factor, factor2: Factor): GaussianFactor = {

    val parentFactor = factor1.asInstanceOf[GaussianFactor]
    val childFactor = factor2.asInstanceOf[GaussianFactor]

    val marginalGaussian = marginalVarId match {
      case `parentVarId` => {

        if (!childFactor.m.isNaN && !childFactor.v.isPosInfinity) {
          if (a == 1 && b == 0) Gaussian(childFactor.m, childFactor.v + v) * Gaussian(parentFactor.m, parentFactor.v)
          else {
            val linearCanonGaussian = CanonicalGaussian(Array(parentVarId, varId), Matrix(a), b, v)
            val productCanonGaussian = CanonicalGaussianMultiply.*(linearCanonGaussian.varIds, linearCanonGaussian, CanonicalGaussian(varId, childFactor.m, childFactor.v)).marginalise(varId).toGaussian()
            val marginal = productCanonGaussian * Gaussian(parentFactor.m, parentFactor.v)
            marginal
          }

        } else if (!parentFactor.m.isNaN && !parentFactor.v.isPosInfinity) {
          val marginal = (LinearGaussian(a, b, v) * Gaussian(parentFactor.m, parentFactor.v)).marginalise(1).toGaussian()
          marginal
        } else Gaussian(0, Double.PositiveInfinity)

      }
      case `varId` => {
        val marginal = if (a == 1 && b == 0) Gaussian(parentFactor.m, parentFactor.v + v) * Gaussian(childFactor.m, childFactor.v)
        else (LinearGaussian(a, b, v) * Gaussian(parentFactor.m, parentFactor.v)).marginalise(0).toGaussian() * Gaussian(childFactor.m, childFactor.v)
        marginal
      }
      case _ => throw new IllegalArgumentException("Incorrect marginal variable id")
    }

    val marginalFactor = GaussianFactor(marginalVarId, marginalGaussian.m, marginalGaussian.v)
    marginalFactor

  }

  def withEvidence(varId: Int, varValue: AnyVal): LinearGaussianFactor = throw new UnsupportedOperationException("Not implemented yet")

  def getValue(assignment: (Int, AnyVal)*): Double = throw new UnsupportedOperationException("Not implemented yet")

  def *(factor: Factor): BivariateGaussianFactor = {

    factor match {
      case factor: GaussianFactor => {
        val gaussianFactor = factor.asInstanceOf[GaussianFactor]
        require(gaussianFactor.varId == parentVarId || gaussianFactor.varId == varId, "Incorrect gaussian variable id")

        val linearCanonGaussian = CanonicalGaussian(Array(parentVarId, varId), Matrix(a), b, v)

        val productGaussian = linearCanonGaussian * CanonicalGaussian(gaussianFactor.varId, gaussianFactor.m, gaussianFactor.v)
        val bivariateGaussianFactor = BivariateGaussianFactor(productGaussian.varIds(0), productGaussian.varIds(1), productGaussian.getMean(), productGaussian.getVariance())
        bivariateGaussianFactor
      }
      case _ => throw new IllegalArgumentException("LinearGaussian factor cannot be multiplied by a factor that is non gaussian")
    }
  }

  def /(that: Factor): LinearGaussianFactor = throw new UnsupportedOperationException("Not implemented yet")

  def equals(that: Factor, threshold: Double): Boolean = throw new UnsupportedOperationException("Not implemented yet")
}