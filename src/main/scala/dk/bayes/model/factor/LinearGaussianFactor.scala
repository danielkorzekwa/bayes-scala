package dk.bayes.model.factor

import dk.bayes.gaussian.CanonicalGaussian
import dk.bayes.gaussian.CanonicalGaussian
import dk.bayes.gaussian.Linear._
import dk.bayes.gaussian.CanonicalGaussianMultiply
import dk.bayes.gaussian.LinearGaussian
import dk.bayes.gaussian.Gaussian

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
case class LinearGaussianFactor(parentVarId: Int, varId: Int, a: Double, b: Double, v: Double) extends Factor {

  def getVariableIds(): Seq[Int] = Vector(parentVarId, varId)

  def marginal(varId: Int): Factor = GaussianFactor(varId, Double.NaN, Double.PositiveInfinity)

  def productMarginal(marginalVarId: Int, factors: Seq[Factor]): GaussianFactor = {

    factors match {
      case Seq(GaussianFactor(`parentVarId`, g1m, g1v), GaussianFactor(`varId`, g2m, g2v)) => {

        val marginalGaussian = marginalVarId match {
          case `parentVarId` => {

            if (!g2m.isNaN && !g2v.isPosInfinity) {
              if (a == 1 && b == 0) Gaussian(g2m, g2v + v) * Gaussian(g1m, g1v)
              else {
                val linearCanonGaussian = CanonicalGaussian(Array(parentVarId, varId), Matrix(a), b, v)
                val productCanonGaussian = CanonicalGaussianMultiply.*(linearCanonGaussian.varIds, linearCanonGaussian, CanonicalGaussian(varId, g2m, g2v)).marginalise(varId).toGaussian()
                val marginal = productCanonGaussian * Gaussian(g1m, g1v)
                marginal
              }

            } else if (!g1m.isNaN && !g1v.isPosInfinity) {
              val marginal = (LinearGaussian(a, b, v) * Gaussian(g1m, g1v)).marginalise(1).toGaussian()
              marginal
            } else Gaussian(Double.NaN, Double.PositiveInfinity)

          }
          case `varId` => {
            val marginal = (LinearGaussian(a, b, v) * Gaussian(g1m, g1v)).marginalise(0).toGaussian() * Gaussian(g2m, g2v)
            marginal
          }
          case _ => throw new IllegalArgumentException("Incorrect marginal variable id")
        }

        val marginalFactor = GaussianFactor(marginalVarId, marginalGaussian.m, marginalGaussian.v)
        marginalFactor
      }
      case _ => throw new IllegalArgumentException("LinearGaussianFactor can be multiplied by exactly two gaussians only, one for each of parentVarId and varId variables")
    }

  }

  def withEvidence(varId: Int, varValue: AnyVal): LinearGaussianFactor = throw new UnsupportedOperationException("Not implemented yet")

  def getValue(assignment: (Int, AnyVal)*): Double = throw new UnsupportedOperationException("Not implemented yet")

  def *(factor: Factor): Factor = throw new UnsupportedOperationException("Not implemented yet")

  def /(that: Factor): Factor = throw new UnsupportedOperationException("Not implemented yet")

  def equals(that: Factor, threshold: Double): Boolean = throw new UnsupportedOperationException("Not implemented yet")
}