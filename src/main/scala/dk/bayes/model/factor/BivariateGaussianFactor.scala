package dk.bayes.model.factor

import dk.bayes.gaussian.Linear._
import dk.bayes.gaussian.CanonicalGaussian
import dk.bayes.model.factor.api.DoubleFactor
import dk.bayes.model.factor.api.Factor
import dk.bayes.model.factor.api.SingleFactor

/**
 * This class represents bivariate Gaussian Factor.
 *
 * @author Daniel Korzekwa
 */
case class BivariateGaussianFactor(parentVarId: Int, varId: Int, mean: Matrix, variance: Matrix) extends DoubleFactor {

  def getVariableIds(): Seq[Int] = Vector(parentVarId, varId)

  def marginal(varId: Int): SingleFactor = throw new UnsupportedOperationException("Not implemented yet")

  def outgoingMessages(factor1: Factor, factor2: Factor): Tuple2[SingleFactor, SingleFactor] = throw new UnsupportedOperationException("Not implemented yet")
  
  override def *(factor: Factor): BivariateGaussianFactor = {
    factor match {
      case factor: GaussianFactor => {
        val gaussianFactor = factor.asInstanceOf[GaussianFactor]
        require(gaussianFactor.varId == parentVarId || gaussianFactor.varId == varId, "Incorrect gaussian variable id.")

        val canonGaussian = CanonicalGaussian(Array(parentVarId, varId), mean, variance)

        val productGaussian = canonGaussian * CanonicalGaussian(gaussianFactor.varId, gaussianFactor.m, gaussianFactor.v)
        val bivariateGaussianFactor = BivariateGaussianFactor(productGaussian.varIds(0), productGaussian.varIds(1), productGaussian.getMean(), productGaussian.getVariance())
        bivariateGaussianFactor
      }
      case _ => throw new IllegalArgumentException("BivariateGaussian factor cannot be multiplied by a factor that is non gaussian")
    }
  }

}