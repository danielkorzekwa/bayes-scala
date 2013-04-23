package dk.bayes.model.factor

import dk.bayes.gaussian.CanonicalGaussian
import dk.bayes.gaussian.CanonicalGaussian
import dk.bayes.gaussian.Linear._

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
    factors.foreach(f => require(f.isInstanceOf[GaussianFactor], "Linear Gaussian factor cannot be multiplied by non gaussian factor:" + f))
    require(!factors.isEmpty, "Linear Gaussian factor cannot be multiplied by an empty list of factors")
    factors.asInstanceOf[Seq[GaussianFactor]].foreach(f => require(f.varId == parentVarId || f.varId == varId, "Incorrect factor variable id: " + f.varId))

    val factorGaussian = CanonicalGaussian(Array(parentVarId, varId), Matrix(a), b, v)
    val otherFactorGaussians = factors.asInstanceOf[Seq[GaussianFactor]].filter(f => !f.m.isNaN && !f.v.isPosInfinity).map(f => CanonicalGaussian(f.varId, f.m, f.v))
    val factorProduct = otherFactorGaussians.foldLeft(factorGaussian)((f1, f2) => f1 * f2)

    val marginalGaussian = marginalVarId match {
      case `parentVarId` => factorProduct.marginalise(varId)
      case `varId` => factorProduct.marginalise(parentVarId)
      case _ => throw new IllegalArgumentException("Incorrect marginal variable id")
    }

    val marginalFactor = GaussianFactor(marginalVarId, marginalGaussian.toGaussian.m, marginalGaussian.toGaussian.v)
    marginalFactor
  }

  def withEvidence(varId: Int, varValue: AnyVal): LinearGaussianFactor = throw new UnsupportedOperationException("Not implemented yet")

  def getValue(assignment: (Int, AnyVal)*): Double = throw new UnsupportedOperationException("Not implemented yet")

  def *(factor: Factor): Factor = throw new UnsupportedOperationException("Not implemented yet")

  def /(that: Factor): Factor = throw new UnsupportedOperationException("Not implemented yet")

  def equals(that: Factor, threshold: Double): Boolean = throw new UnsupportedOperationException("Not implemented yet")
}