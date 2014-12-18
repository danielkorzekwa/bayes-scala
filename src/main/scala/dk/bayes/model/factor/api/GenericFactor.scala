package dk.bayes.model.factor.api

/**
 * Factor with any number of variables.
 *
 *  @author Daniel Korzekwa
 */
trait GenericFactor extends Factor{

   type FACTOR_TYPE = GenericFactor
  
  /**Returns outgoing messages to factor variables. List[var1 msg, var2 msg,...,varn msg]
   * 
   * @param msgsIn Incoming messages
   * */
  def outgoingMessages(msgsIn:Seq[SingleFactor]):Seq[SingleFactor]
}