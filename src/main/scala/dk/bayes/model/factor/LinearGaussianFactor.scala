package dk.bayes.model.factor

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

  def marginal(varId: Int): Factor = throw new UnsupportedOperationException("Not implemented yet")

  def withEvidence(varId: Int, varValue: AnyVal): TableFactor = throw new UnsupportedOperationException("Not implemented yet")

  def getValue(assignment: (Int, AnyVal)*): Double = throw new UnsupportedOperationException("Not implemented yet")

  def *(factor: Factor): Factor = throw new UnsupportedOperationException("Not implemented yet")
}