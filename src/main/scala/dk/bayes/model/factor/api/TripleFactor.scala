package dk.bayes.model.factor.api

/**
 * Factor with three variables.
 *
 * @author Daniel Korzekwa
 */
trait TripleFactor extends Factor {

    type FACTOR_TYPE = TripleFactor
  
  /**
   * Returns a marginal for a given variable of a product of this and other factors.
   */
  def productMarginal(varId: Int, factor1:Factor,factor2:Factor,factor3:Factor): SingleFactor
 
}