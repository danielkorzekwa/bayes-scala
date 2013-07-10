package dk.bayes.model.factor.api

/**
 * Factor with two variables.
 *
 * @author Daniel Korzekwa
 */
trait DoubleFactor extends Factor {

  type FACTOR_TYPE = DoubleFactor

  /**
   * Returns a marginal for a given variable of a product of this and other factors.
   */
  def productMarginal(varId: Int, factor1: Factor, factor2: Factor): SingleFactor

}