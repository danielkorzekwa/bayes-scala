package dk.bayes.model.factor

import dk.bayes.gaussian.Gaussian

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
case class TruncGaussianFactor(gaussianVarId: Int, truncVarId: Int, truncValue: Double, truncVarEvidence: Option[Boolean] = None) extends Factor {

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

  def productMarginal(varId: Int, factors: Seq[Factor]): SingleFactor = {

    factors match {
      case Seq(gaussianFactor: GaussianFactor, tableFactor: SingleTableFactor) if (gaussianFactor.varId == gaussianVarId) &&
        tableFactor.varId == truncVarId &&
        tableFactor.variableDim == 2 => {

        val marginalFactor = varId match {
          case `gaussianVarId` => {

            val marginalGaussian = truncVarEvidence match {
              case None => Gaussian(gaussianFactor.m, gaussianFactor.v)
              case Some(true) => Gaussian(gaussianFactor.m, gaussianFactor.v).truncate(truncValue, true)
              case Some(false) => Gaussian(gaussianFactor.m, gaussianFactor.v).truncate(truncValue, false)
            }

            GaussianFactor(varId, marginalGaussian.m, marginalGaussian.v)

          }
          case `truncVarId` => {

            truncVarEvidence match {
              case None => {
                val value0Prob = 1 - Gaussian(gaussianFactor.m, gaussianFactor.v).cdf(truncValue)
                val value1Prob = 1 - value0Prob
                val valueProbs = Array(value0Prob, value1Prob)

                SingleTableFactor(varId, 2, valueProbs)
              }
              case Some(truncVarEvidence) => outcomeMarginal(truncVarEvidence)
            }
          }
          case _ => throw new IllegalArgumentException("Incorrect marginal variable id")
        }

        marginalFactor

      }
      case _ => throw new IllegalArgumentException("TruncGaussianFactor can be multiplied by exactly two factors only, GaussianFactor and TableFactor")
    }

  }

  def withEvidence(varId: Int, varValue: AnyVal): TruncGaussianFactor = {
    varId match {
      case `gaussianVarId` => throw new UnsupportedOperationException("Setting the evidence value for the gaussian variable of TruncGaussianFactor is not supported")
      case `truncVarId` => {
        this.copy(truncVarEvidence = Some(varValue.asInstanceOf[Boolean]))
      }
    }
  }

  def getValue(assignment: (Int, AnyVal)*): Double = throw new UnsupportedOperationException("Not implemented yet")

  def *(factor: Factor): Factor = throw new UnsupportedOperationException("Not implemented yet")

  def /(that: Factor): Factor = throw new UnsupportedOperationException("Not implemented yet")

  def equals(that: Factor, threshold: Double): Boolean = throw new UnsupportedOperationException("Not implemented yet")

  private def outcomeMarginal(truncVarEvidence: Boolean): SingleTableFactor = {
    truncVarEvidence match {
      case true => SingleTableFactor(truncVarId, 2, Array(1 - ZERO_PROBABILITY, ZERO_PROBABILITY))
      case false => SingleTableFactor(truncVarId, 2, Array(ZERO_PROBABILITY, 1 - ZERO_PROBABILITY))
    }
  }
}