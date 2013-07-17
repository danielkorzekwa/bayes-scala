package dk.bayes.model.factor

import dk.bayes.gaussian._
import dk.bayes.model.factor._
import dk.bayes.model.factor.api.Factor
import dk.bayes.model.factor.api.SingleFactor
import dk.bayes.model.factor.api.TripleFactor

/**
 * This class represents a factor for the difference of two Gaussian distributions.
 *
 * @author Daniel Korzekwa
 *
 * diffGaussian = gaussian1 - gaussian2
 *
 * @param gaussian1VarId
 * @param gaussian2VarId
 * @param diffGaussianVarId
 */
case class DiffGaussianFactor(gaussian1VarId: Int, gaussian2VarId: Int, diffGaussianVarId: Int) extends TripleFactor {

  require(gaussian1VarId != gaussian2VarId && gaussian2VarId != diffGaussianVarId, "DiffGaussian variable ids are not unique")

  def getVariableIds(): Seq[Int] = Vector(gaussian1VarId, gaussian2VarId, diffGaussianVarId)

  def marginal(varId: Int): GaussianFactor = GaussianFactor(varId, 0, Double.PositiveInfinity)

  def productMarginal(varId: Int, factor1: Factor, factor2: Factor, factor3: Factor): SingleFactor = {

    val gaussianFactor1 = factor1.asInstanceOf[GaussianFactor]
    val gaussianFactor2 = factor2.asInstanceOf[GaussianFactor]
    val diffFactor = factor3.asInstanceOf[GaussianFactor]

    val marginalFactor = varId match {

      case `gaussian1VarId` => {
        val sumGaussian = (Gaussian(diffFactor.m, diffFactor.v) + Gaussian(gaussianFactor2.m, gaussianFactor2.v)) * Gaussian(gaussianFactor1.m, gaussianFactor1.v)
        GaussianFactor(varId, sumGaussian.m, sumGaussian.v)
      }

      case `gaussian2VarId` => {
        val diffGaussian = (Gaussian(gaussianFactor1.m, gaussianFactor1.v) - Gaussian(diffFactor.m, diffFactor.v)) * Gaussian(gaussianFactor2.m, gaussianFactor2.v)
        GaussianFactor(varId, diffGaussian.m, diffGaussian.v)
      }

      case `diffGaussianVarId` => {
        val diffGaussian = (Gaussian(gaussianFactor1.m, gaussianFactor1.v) - Gaussian(gaussianFactor2.m, gaussianFactor2.v)) * Gaussian(diffFactor.m, diffFactor.v)
        GaussianFactor(varId, diffGaussian.m, diffGaussian.v)
      }

      case _ => throw new IllegalArgumentException("Incorrect marginal variable id")
    }

    marginalFactor
  }

  def productMarginal(varId: Int, factors: Seq[Factor]): GaussianFactor = {
    factors.foreach(f => require(f.isInstanceOf[GaussianFactor], "DiffGaussian factor cannot be multiplied by non gaussian factor:" + f))
    require(factors.size == 3, "DiffGaussianFactor can be only multiplied by exactly three gaussian factors, one for each DiffGaussianFactor variable")

    val gaussianFactor1 = factors.asInstanceOf[Seq[GaussianFactor]].find(f => f.varId == gaussian1VarId).get
    val gaussianFactor2 = factors.asInstanceOf[Seq[GaussianFactor]].find(f => f.varId == gaussian2VarId).get
    val diffFactor = factors.asInstanceOf[Seq[GaussianFactor]].find(f => f.varId == diffGaussianVarId).get

    val marginalFactor = varId match {

      case `gaussian1VarId` => {
        val sumGaussian = (Gaussian(diffFactor.m, diffFactor.v) + Gaussian(gaussianFactor2.m, gaussianFactor2.v)) * Gaussian(gaussianFactor1.m, gaussianFactor1.v)
        GaussianFactor(varId, sumGaussian.m, sumGaussian.v)
      }

      case `gaussian2VarId` => {
        val diffGaussian = (Gaussian(gaussianFactor1.m, gaussianFactor1.v) - Gaussian(diffFactor.m, diffFactor.v)) * Gaussian(gaussianFactor2.m, gaussianFactor2.v)
        GaussianFactor(varId, diffGaussian.m, diffGaussian.v)
      }

      case `diffGaussianVarId` => {
        val diffGaussian = (Gaussian(gaussianFactor1.m, gaussianFactor1.v) - Gaussian(gaussianFactor2.m, gaussianFactor2.v)) * Gaussian(diffFactor.m, diffFactor.v)
        GaussianFactor(varId, diffGaussian.m, diffGaussian.v)
      }

      case _ => throw new IllegalArgumentException("Incorrect marginal variable id")
    }

    marginalFactor
  }

}