package dk.bayes.dsl

/**
 * Inference engine for computing variable posterior in a Bayesian Network
 *
 * @author Daniel Korzekwa
 */
trait InferEngine[FROM, TO] {

  def infer(x: FROM): TO

  /**
   * Returns true if the variable can be inferred by the inference engine
   */
  def isSupported(x: FROM): Boolean
}