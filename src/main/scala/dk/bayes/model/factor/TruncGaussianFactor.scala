package dk.bayes.model.factor

import dk.bayes.math.gaussian.Gaussian
import dk.bayes.model.factor.api.Factor
import dk.bayes.model.factor.api.DoubleFactor
import dk.bayes.model.factor.api.SingleFactor

/**
 * This factor represents a truncated Gaussian distribution.
 *
 * @author Daniel Korzekwa
 *
 * @param gaussianVarId
 * @param truncVarId
 * @param truncValue The value, at which Gaussian distribution is truncated at
 * @param truncVarEvidence The evidence for the binary truncation variable.
 *        Its value is true when the Gaussian variable value is bigger than the truncValue, and it's false if it is lower than the truncValue.
 *        It's not defined if there is no evidence observed
 */
case class TruncGaussianFactor(gaussianVarId: Int, truncVarId: Int, truncValue: Double, truncVarEvidence: Option[Boolean] = None) extends DoubleFactor {

  private val ZERO_PROBABILITY = 1.0E-20

  def getVariableIds(): Seq[Int] = Vector(gaussianVarId, truncVarId)

  def marginal(varId: Int): SingleFactor = {

    val marginalFactor = varId match {
      case `gaussianVarId` => GaussianFactor(varId, 0, Double.PositiveInfinity)

      case `truncVarId` => {
        truncVarEvidence match {
          case None => SingleTableFactor(varId, 2, Array(1d, 1d))
          case Some(truncVarEvidence) => outcomeMarginal(truncVarEvidence)
        }

      }
      case _ => throw new IllegalArgumentException("Unknown variable id: " + varId)
    }

    marginalFactor
  }

  def outgoingMessages(factor1: Factor, factor2: Factor): Tuple2[GaussianFactor, SingleTableFactor] = {
    outgoingMessagesInternal(factor1.asInstanceOf[GaussianFactor], factor2.asInstanceOf[SingleTableFactor])
  }
  private def outgoingMessagesInternal(gaussianFactor: GaussianFactor, tableFactor: SingleTableFactor): Tuple2[GaussianFactor, SingleTableFactor] = {

    val gaussian = truncVarEvidence match {
      case None => Gaussian(0,Double.PositiveInfinity)
      case Some(true) => Gaussian(gaussianFactor.m, gaussianFactor.v).truncate(truncValue, true) / Gaussian(gaussianFactor.m, gaussianFactor.v)
      case Some(false) => Gaussian(gaussianFactor.m, gaussianFactor.v).truncate(truncValue, false) / Gaussian(gaussianFactor.m, gaussianFactor.v)
    }
    val gaussianMsg = GaussianFactor(gaussianVarId, gaussian.m, gaussian.v)

    val truncMsg = truncVarEvidence match {
      case None => {
        val value0Prob = 1 - Gaussian(gaussianFactor.m, gaussianFactor.v).cdf(truncValue)
        val value1Prob = 1 - value0Prob
        val valueProbs = Array(value0Prob, value1Prob)

        SingleTableFactor(truncVarId, 2, valueProbs) / tableFactor
      }
      case Some(truncVarEvidence) => outcomeMarginal(truncVarEvidence) / tableFactor
    }

    Tuple2(gaussianMsg, truncMsg)
  }

  override def withEvidence(varId: Int, varValue: AnyVal): TruncGaussianFactor = {
    varId match {
      case `gaussianVarId` => throw new UnsupportedOperationException("Setting the evidence value for the gaussian variable of TruncGaussianFactor is not supported")
      case `truncVarId` => {
        this.copy(truncVarEvidence = Some(varValue.asInstanceOf[Boolean]))
      }
    }
  }

  private def outcomeMarginal(truncVarEvidence: Boolean): SingleTableFactor = {
    truncVarEvidence match {
      case true => SingleTableFactor(truncVarId, 2, Array(1 - ZERO_PROBABILITY, ZERO_PROBABILITY))
      case false => SingleTableFactor(truncVarId, 2, Array(ZERO_PROBABILITY, 1 - ZERO_PROBABILITY))
    }
  }
}