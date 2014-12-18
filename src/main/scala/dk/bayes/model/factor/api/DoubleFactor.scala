package dk.bayes.model.factor.api

/**
 * Factor with two variables.
 *
 * @author Daniel Korzekwa
 */
trait DoubleFactor extends Factor {

  type FACTOR_TYPE = DoubleFactor
  
  /**Returns outgoing messages to factor variables. Tuple2[var1 msg, var2 msg]*/
  def outgoingMessages(factor1: Factor, factor2: Factor):Tuple2[SingleFactor,SingleFactor]

}