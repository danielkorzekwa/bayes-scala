package dk.bayes.model.factor

/**
 * Single variable factor.
 *
 * @author Daniel Korzekwa
 */
trait SingleFactor extends Factor {

  /**
   * Returns factor variable identifier.
   */
  def getVariableId(): Int

  override def *(factor: Factor): SingleFactor

  override def /(factor: Factor): SingleFactor
}