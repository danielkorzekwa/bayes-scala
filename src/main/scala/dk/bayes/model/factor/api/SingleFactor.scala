package dk.bayes.model.factor.api

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