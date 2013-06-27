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
 *        Its value is 0 when the Gaussian variable value is bigger than the truncValue, and it's 1 if it is lower than the truncValue.
 *        It's not defined if there is no evidence observed
 */
case class TruncGaussianFactor(gaussianVarId: Int, truncVarId: Int, truncValue: Double, truncVarEvidence: Option[Int] = None) extends Factor {

  require(truncVarEvidence.isEmpty || truncVarEvidence.get == 0 || truncVarEvidence.get == 1, "The evidence for the binary truncation variable can take the values 0 or 1")

  def getVariableIds(): Seq[Int] = Vector(gaussianVarId, truncVarId)

  def marginal(varId: Int): Factor = {

    val marginalFactor = varId match {
      case `gaussianVarId` => GaussianFactor(varId, 0, Double.PositiveInfinity)

      case `truncVarId` => {
        truncVarEvidence match {
          case None => TableFactor(Vector(varId), Vector(2), Array(1d, 1d))
          case Some(0) => TableFactor(Vector(varId), Vector(2), Array(1d, 0d))
          case Some(1) => TableFactor(Vector(varId), Vector(2), Array(0d, 1d))
          case _ => throw new IllegalArgumentException("The evidence for the binary truncation variable can take the values 0 or 1")
        }

      }
      case _ => throw new IllegalArgumentException("Unknown variable id: " + varId)
    }

    marginalFactor
  }

  def productMarginal(varId: Int, factors: Seq[Factor]): Factor = {

    factors match {
      case Seq(gaussianFactor: GaussianFactor, tableFactor: TableFactor) if (gaussianFactor.varId == gaussianVarId) &&
        tableFactor.variableIds.size == 1 &&
        tableFactor.variableIds.head == truncVarId &&
        tableFactor.variableDims.size == 1 &&
        tableFactor.variableDims.head == 2 => {

        val truncFactorProduct = (tableFactor * marginal(truncVarId))

        val marginalFactor = varId match {
          case `gaussianVarId` => {

            val marginalGaussian =
              if (truncFactorProduct.valueProbs(0) == 1 && truncFactorProduct.valueProbs(1) == 0) Gaussian(gaussianFactor.m, gaussianFactor.v).truncate(truncValue, true)
              else if (truncFactorProduct.valueProbs(0) == 0 && truncFactorProduct.valueProbs(1) == 1) Gaussian(gaussianFactor.m, gaussianFactor.v).truncate(truncValue, false)
              else if (truncFactorProduct.valueProbs(0) == 1 && truncFactorProduct.valueProbs(1) == 1) Gaussian(gaussianFactor.m, gaussianFactor.v)
              else throw new IllegalArgumentException("Not implemented yet")

            GaussianFactor(varId, marginalGaussian.m, marginalGaussian.v)

          }
          case `truncVarId` => {
            if (truncFactorProduct.valueProbs(0) == 1 && truncFactorProduct.valueProbs(1) == 1) {
              val value0Prob = 1 - Gaussian(gaussianFactor.m, gaussianFactor.v).cdf(truncValue)
              val value1Prob = 1 - value0Prob
              val valueProbs = Array(value0Prob, value1Prob)

              TableFactor(Vector(varId), Vector(2), valueProbs)
            } else if (truncFactorProduct.valueProbs(0) == 1 && truncFactorProduct.valueProbs(1) == 0) TableFactor(Vector(varId), Vector(2), Array(1, 0))
            else if (truncFactorProduct.valueProbs(0) == 0 && truncFactorProduct.valueProbs(1) == 1) TableFactor(Vector(varId), Vector(2), Array(0, 1))
            else throw new IllegalArgumentException("Not implemented yet")

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
        this.copy(truncVarEvidence = Some(varValue.asInstanceOf[Int]))
      }
    }
  }

  def getValue(assignment: (Int, AnyVal)*): Double = throw new UnsupportedOperationException("Not implemented yet")

  def *(factor: Factor): Factor = throw new UnsupportedOperationException("Not implemented yet")

  def /(that: Factor): Factor = throw new UnsupportedOperationException("Not implemented yet")

  def equals(that: Factor, threshold: Double): Boolean = throw new UnsupportedOperationException("Not implemented yet")
}