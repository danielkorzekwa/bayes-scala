package dk.bayes.model.factor

import dk.bayes.math.gaussian._
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

  def outgoingMessages(factor1: Factor, factor2: Factor, factor3: Factor): Tuple3[GaussianFactor, GaussianFactor, GaussianFactor] = {
    outgoingMessagesInternal(factor1.asInstanceOf[GaussianFactor], factor2.asInstanceOf[GaussianFactor], factor3.asInstanceOf[GaussianFactor])
  }

  private def outgoingMessagesInternal(gaussianFactor1: GaussianFactor, gaussianFactor2: GaussianFactor, diffFactor: GaussianFactor): Tuple3[GaussianFactor, GaussianFactor, GaussianFactor] = {

    val var1Gaussian = (Gaussian(diffFactor.m, diffFactor.v) + Gaussian(gaussianFactor2.m, gaussianFactor2.v))
    val var1Msg = GaussianFactor(gaussian1VarId, var1Gaussian.m, var1Gaussian.v)

    val var2Gaussian = (Gaussian(gaussianFactor1.m, gaussianFactor1.v) - Gaussian(diffFactor.m, diffFactor.v))
    val var2Msg = GaussianFactor(gaussian2VarId, var2Gaussian.m, var2Gaussian.v)

    val diffGaussian = (Gaussian(gaussianFactor1.m, gaussianFactor1.v) - Gaussian(gaussianFactor2.m, gaussianFactor2.v))
    val diffMsg = GaussianFactor(diffGaussianVarId, diffGaussian.m, diffGaussian.v)

    Tuple3(var1Msg, var2Msg, diffMsg)
  }

}