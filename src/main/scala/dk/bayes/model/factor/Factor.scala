package dk.bayes.model.factor

/**
 * Represents a probability distribution over a set of variables in a factor graph.
 *
 * @author Daniel Korzekwa
 */
trait Factor {

  /**
   * Returns new factor with a given evidence.
   *
   * @param varId  Variable id
   * @param varValue Variable value
   */
  def withEvidence(varId: Int, varValue: AnyVal): Factor

  /**
   * Returns the factor value for a given assignment to all factor variables.
   *
   * @param assignment Assignment to all factor variables (varId,varValue)
   */
  def getValue(assignment: (Int,AnyVal)*): Double
}