package dk.bayes.infer.ep.util

import dk.bayes.math.gaussian._
import dk.bayes.model.factor._
import dk.bayes.model.factor.api.Factor
import dk.bayes.model.factor.api.TripleFactor
import dk.bayes.model.factor.GaussianFactor
import dk.bayes.model.factor.api.GenericFactor
import dk.bayes.model.factor.api.SingleFactor

/**
 * This class represents a factor for the difference of two Gaussian distributions.
 *
 * @author Daniel Korzekwa
 *
 * diffGaussian = gaussian1 - gaussian2
 *
 * @param varIds (gaussian1VarId,gaussian2VarId,diffGaussianVarId)
 */
case class GenericDiffGaussianFactor(varIds: Seq[Int]) extends GenericFactor {

  val gaussian1VarId = varIds(0)
  val gaussian2VarId = varIds(1)
  val diffGaussianVarId = varIds(2)

  require(gaussian1VarId != gaussian2VarId && gaussian2VarId != diffGaussianVarId, "DiffGaussian variable ids are not unique")

  def getVariableIds(): Seq[Int] = Vector(gaussian1VarId, gaussian2VarId, diffGaussianVarId)

  def marginal(varId: Int): GaussianFactor = GaussianFactor(varId, 0, Double.PositiveInfinity)

  def outgoingMessages(msgsIn: Seq[SingleFactor]): Seq[SingleFactor] = {
    outgoingMessagesInternal(msgsIn(0).asInstanceOf[GaussianFactor], msgsIn(1).asInstanceOf[GaussianFactor], msgsIn(2).asInstanceOf[GaussianFactor])
  }

  private def outgoingMessagesInternal(gaussianFactor1: GaussianFactor, gaussianFactor2: GaussianFactor, diffFactor: GaussianFactor): Seq[SingleFactor] = {

    val var1Gaussian = (Gaussian(diffFactor.m, diffFactor.v) + Gaussian(gaussianFactor2.m, gaussianFactor2.v))
    val var1Msg = GaussianFactor(gaussian1VarId, var1Gaussian.m, var1Gaussian.v)

    val var2Gaussian = (Gaussian(gaussianFactor1.m, gaussianFactor1.v) - Gaussian(diffFactor.m, diffFactor.v))
    val var2Msg = GaussianFactor(gaussian2VarId, var2Gaussian.m, var2Gaussian.v)

    val diffGaussian = (Gaussian(gaussianFactor1.m, gaussianFactor1.v) - Gaussian(gaussianFactor2.m, gaussianFactor2.v))
    val diffMsg = GaussianFactor(diffGaussianVarId, diffGaussian.m, diffGaussian.v)

    List(var1Msg, var2Msg, diffMsg)
  }

}