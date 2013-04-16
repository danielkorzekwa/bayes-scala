package dk.bayes.model.factor

import dk.bayes.gaussian.Gaussian

/**
 * This factor represents a truncated Gaussian distribution.
 *
 * @author Daniel Korzekwa
 *
 * @param gaussianVarId
 * @param truncVarId
 * @param truncValue The value, at which Gaussian distribution is truncated at.
 */
case class TruncGaussianFactor(gaussianVarId: Int, truncVarId: Int, truncValue: Double) extends Factor {

  def getVariableIds(): Seq[Int] = Vector(gaussianVarId, truncVarId)

  def marginal(varId: Int): Factor = {

    val marginalFactor = varId match {
      case `gaussianVarId` => GaussianFactor(varId, Double.NaN, Double.PositiveInfinity)
      case `truncVarId` => TableFactor(Vector(varId), Vector(2), Array(1d, 1d))
      case _ => throw new IllegalArgumentException("Unknown variable id: " + varId)
    }

    marginalFactor
  }

  def productMarginal(varId: Int, factors: Seq[Factor]): Factor = {
    val gaussianFactor = factors.find(f => f.getVariableIds().head == gaussianVarId).get.asInstanceOf[GaussianFactor]
    val tableFactor = factors.find(f => f.getVariableIds().head == truncVarId).get.asInstanceOf[TableFactor]
    require(tableFactor.valueProbs.size == 2, "Trunc Factor must be a binary table factor")

    val marginalFactor = varId match {
      case `gaussianVarId` => {
        val marginalGaussian = tableFactor.getEvidence(truncVarId) match {
          case Some(0) => Gaussian(gaussianFactor.m, gaussianFactor.v).truncateUpperTail(truncValue)
          case Some(1) => throw new UnsupportedOperationException("Not implemented yet")
          case None => {
            require(tableFactor.valueProbs.product == 1, "Unity Table factor is required")
            Gaussian(gaussianFactor.m, gaussianFactor.v)
          }
          case _ => throw new IllegalArgumentException("Incorrect evidence value for a binary table factor")
        }

        GaussianFactor(varId, marginalGaussian.m, marginalGaussian.v)

      }
      case `truncVarId` => tableFactor.getEvidence(truncVarId) match {
        case None if (tableFactor.valueProbs.product == 1) => {
          val valueProbs = if (!gaussianFactor.m.isNaN && !gaussianFactor.v.isNaN) {
            val value0Prob = 1 - Gaussian(gaussianFactor.m, gaussianFactor.v).cdf(truncValue)
            val value1Prob = 1 - value0Prob
            Array(value0Prob, value1Prob)

          } else Array(1d, 1d)
          TableFactor(Vector(varId), Vector(2), valueProbs)
        }
        case _ => throw new UnsupportedOperationException("Not implemented yet")
      }

      case _ => throw new IllegalArgumentException("Incorrect marginal variable id")
    }

    marginalFactor
  }

  def withEvidence(varId: Int, varValue: AnyVal): TruncGaussianFactor = throw new UnsupportedOperationException("Not implemented yet")

  def getEvidence(varId: Int): Option[AnyVal] = throw new UnsupportedOperationException("Not implemented yet")

  def getValue(assignment: (Int, AnyVal)*): Double = throw new UnsupportedOperationException("Not implemented yet")

  def *(factor: Factor): Factor = throw new UnsupportedOperationException("Not implemented yet")

  def /(that: Factor): Factor = throw new UnsupportedOperationException("Not implemented yet")
}