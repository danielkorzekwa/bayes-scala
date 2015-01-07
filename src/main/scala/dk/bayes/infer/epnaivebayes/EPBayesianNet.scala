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
   * Returns integral p(x)*p(x|y) dy
   */
  def marginalX(x: X, y: Y): X
  def isIdentical(x1: X, x2: X, threshold: Double): Boolean
}