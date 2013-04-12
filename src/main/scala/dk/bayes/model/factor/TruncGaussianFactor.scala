package dk.bayes.model.factor

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
      case `truncVarId` => TableFactor(Vector(varId), Vector(2), Vector(1d, 1d))
      case _ => throw new IllegalArgumentException("Unknown variable id: " + varId)
    }

    marginalFactor
  }

  def productMarginal(varId: Int, factors: Seq[Factor]): Factor = {
    throw new UnsupportedOperationException("Not implemented yet")
  }

  def withEvidence(varId: Int, varValue: AnyVal): TruncGaussianFactor = throw new UnsupportedOperationException("Not implemented yet")

  def getValue(assignment: (Int, AnyVal)*): Double = throw new UnsupportedOperationException("Not implemented yet")

  def *(factor: Factor): Factor = throw new UnsupportedOperationException("Not implemented yet")

  def /(that: Factor): Factor = throw new UnsupportedOperationException("Not implemented yet")
}