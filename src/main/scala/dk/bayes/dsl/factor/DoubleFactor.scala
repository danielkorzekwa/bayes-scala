package dk.bayes.dsl.factor

/**
 * Factor for y = f(x)
 *
 * @author Daniel Korzekwa
 */
trait DoubleFactor[X,Y] {

  val initFactorMsgUp: X
  
   /**
   * @param x Marginal of variable x
   * @param oldFactorMsgUp old message sent from factor y=f(x) to variable X
   *
   *  @returns New message from factor y=f(x) to variable X
   */
  def calcYFactorMsgUp(x: X, oldFactorMsgUp: X): Option[X]
}