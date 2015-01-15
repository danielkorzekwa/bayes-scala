package dk.bayes.infer.epnaivebayes

/**
 * Naive bayes net. Variables: X, Y1|X, Y2|X,...Yn|X
 *
 * @author Daniel Korzekwa
 */
trait EPBayesianNet[X, Y] {

  val prior: X
  val likelihoods: Seq[Y]
  val initFactorMsgUp: X

  def product(x1: X, x2: X): X
  def divide(x1: X, x2: X): X

  /**
   * @param x Marginal of variable x
   * @param oldFactorMsgUp old message sent from factor y=f(x) to variable X
   *
   *  @returns New message from factor y=f(x) to variable X
   */
  def calcYFactorMsgUp(x: X, oldFactorMsgUp: X, y: Y): Option[X]

  def isIdentical(x1: X, x2: X, tolerance: Double): Boolean
}