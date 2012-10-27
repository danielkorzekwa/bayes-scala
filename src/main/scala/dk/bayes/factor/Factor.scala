package dk.bayes.factor

import Factor._
import scala.collection.mutable.LinkedHashMap

/**
 * Represents potentials over a set of variables.
 */
trait Factor {

  /**
   * Returns factor variables.
   */
  def getVariables(): Array[Var]

  /**
   * Returns factor potentials.
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
   * Returns marginal factor for a given variable.
   */
  def marginal(varId: Int): SingleFactor

  /**
   *  Returns new factor with all potential values normalised to 1.
   */
  def normalise(): Factor

}

object Factor {

  /**
   * Represents factor variable.
   *
   * @param name Unique variable identifier.
   * @param values Number of values that this variables can take on.
   *
   */
  case class Var(id: Int, dim: Int)

}