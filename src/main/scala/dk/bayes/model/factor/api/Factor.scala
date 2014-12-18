package dk.bayes.model.factor.api

/**
 * Represents a probability distribution over a set of variables in a factor graph.
 *
 * @author Daniel Korzekwa
 */
trait Factor {
  
  type FACTOR_TYPE <: Factor
  
  /**
   * Returns factor variable identifiers.
   */
  def getVariableIds(): Seq[Int]

  /**
   * Returns marginal factor for a given variable id.
   */
  def marginal(varId: Int): SingleFactor
  
  /**
   * Returns new factor with a given evidence.
   *
   * @param varId  Variable id
   * @param varValue Variable value
   */
  def withEvidence(varId: Int, varValue: AnyVal): FACTOR_TYPE = throw new UnsupportedOperationException("Not supported")
  
  /**
   * Returns the factor value for a given assignment to all factor variables.
   *
   * @param assignment Assignment to all factor variables (varId,varValue)
   */
  def getValue(assignment: (Int, AnyVal)*): Double = throw new UnsupportedOperationException("Not supported")

  /**
   * Returns the product of this and that factor.
   */
  def *(factor: Factor): FACTOR_TYPE = throw new UnsupportedOperationException("Not supported")

  /**
   * Divides this factor by that factor.
   *
   */
  def /(factor: Factor): FACTOR_TYPE = throw new UnsupportedOperationException("Not supported")
  
  /**
   * Returns true if this and that factors are the same
   * with a tolerance level specified by the threshold parameter.
   */
  def equals(that:Factor, threshold:Double):Boolean = throw new UnsupportedOperationException("Not supported")
}