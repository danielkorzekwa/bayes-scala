package dk.bayes.model.factor.api

/**
 * Factor with three variables.
 *
 * @author Daniel Korzekwa
 */
trait TripleFactor extends Factor {

  type FACTOR_TYPE = TripleFactor

  /**Returns outgoing messages to factor variables. Tuple2[var1 msg, var2 msg, var3 msg]*/
  def outgoingMessages(factor1: Factor, factor2: Factor, factor3: Factor): Tuple3[SingleFactor, SingleFactor,SingleFactor]
}