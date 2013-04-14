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
case class TableFactor(variableIds: Seq[Int], variableDims: Seq[Int], values: Array[Double]) extends Factor {

  def getVariableIds(): Seq[Int] = variableIds

  def marginal(varId: Int): TableFactor = throw new UnsupportedOperationException("Not implemented yet")

  def productMarginal(varId: Int, factors: Seq[Factor]): Factor = throw new UnsupportedOperationException("Not implemented yet")

  def withEvidence(varId: Int, varValue: AnyVal): TableFactor = throw new UnsupportedOperationException("Not implemented yet")

  def getEvidence(varId: Int): Option[AnyVal] = throw new UnsupportedOperationException("Not implemented yet")

  def getValue(assignment: (Int, AnyVal)*): Double = {
    val value = variableIds match {
      case Seq(varId) => getValueForSingleFactor(assignment)
      case _ => throw new UnsupportedOperationException("Not implemented yet")
    }

    value
  }

  private def getValueForSingleFactor(assignment: Seq[Tuple2[Int, AnyVal]]): Double = {
    val value = assignment match {
      case Seq((varId: Int, varValue: Int)) if varId == variableIds.head => values(varValue)
      case _ => throw new IllegalArgumentException("Factor assignment does not match single variable factor: " + assignment)
    }
    value
  }

  def *(factor: Factor): Factor = throw new UnsupportedOperationException("Not implemented yet")

  def /(that: Factor): Factor = throw new UnsupportedOperationException("Not implemented yet")
}