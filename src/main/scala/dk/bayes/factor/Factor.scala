package dk.bayes.factor

/**
 * Represents potentials over a set of variables.
 *
 * @author Daniel Korzekwa
 */
trait Factor {

  /**
   * Returns factor variables.
   */
  def getVariables(): Array[Var]

  /**
   * Returns factor potentials.
   *
   * Values indexing:
   *
   * variable0 (dim=3) variable1(dim=2) variable2(dim=2) value
   * -------------------------
   * 0 0 0 val0
   * 0 0 1 val1
   * 0 1 0 val2
   * 0 1 1 val3
   * 1 0 0 val4
   * 1 0 1 val5
   * 1 1 0 val6
   * 1 1 1 val7
   * 2 0 0 val8
   * 2 0 1 val9
   * 2 1 0 val10
   * 2 1 1 val11
   */
  def getValues(): Array[Double]

  /**
   * Returns potential value for a given assignment to all factor variables.
   *
   * @param assignment Array[variable value index]
   */
  def getValue(assignment: Array[Int]): Double

  /**
   *  Returns product of this factor and single factor.
   */
  def product(singleFactor: SingleFactor): Factor

  /**
   * Returns new factor with zero values for all potential entries inconsistent with evidence.
   *
   * @param evidence Tuple2[variable id, variable value index]
   */
  def withEvidence(evidence: Tuple2[Int, Int]): Factor

  /**
   * Returns marginal factor for a given variable id.
   */
  def marginal(varId: Int): SingleFactor

  /**
   *  Returns new factor with all potential values normalised to 1.
   */
  def normalise(): Factor

}

object Factor {

  /**
   * Creates single factor.
   *
   * @param variable Factor variable
   * @param values Factor values
   */
  def apply(variable: Var, values: Array[Double]): SingleFactor = {
    new SingleFactor(variable, values)
  }
}