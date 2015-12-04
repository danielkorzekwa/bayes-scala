package dk.bayes.factorgraph.factor.api


import dk.bayes.factorgraph.factor.api.Factor

/**
 * Single variable factor.
 *
 * @author Daniel Korzekwa
 */
trait SingleFactor extends Factor {

  type FACTOR_TYPE = SingleFactor

  /**
   * Returns factor variable identifier.
   */
  def getVariableId(): Int

  def getVariableIds(): Seq[Int] = Vector(getVariableId())

}