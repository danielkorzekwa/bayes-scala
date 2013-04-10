package dk.bayes.model.factor

/**
 * This class represents a multinomial probability distribution over a set of discrete variables.
 *
 * @author Daniel Korzekwa
 *
 * @param variableIds Factor variables
 * @param variableDims Variable dimensions
 * @param values Factor values
 */
case class TableFactor(variableIds: Seq[Int], variableDims: Seq[Int], values: Seq[Double]) extends Factor {

  def getVariableIds(): Seq[Int] = throw new UnsupportedOperationException("Not implemented yet")

  def marginal(varId: Int): Factor = throw new UnsupportedOperationException("Not implemented yet")

  def withEvidence(varId: Int, varValue: AnyVal): TableFactor = throw new UnsupportedOperationException("Not implemented yet")

  def getValue(assignment: (Int, AnyVal)*): Double = throw new UnsupportedOperationException("Not implemented yet")

  def *(factor: Factor): Factor = throw new UnsupportedOperationException("Not implemented yet")
}