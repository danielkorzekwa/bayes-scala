package dk.bayes.dsl

/**
 * Inference engine for computing variable posterior in a Bayesian Network
 *
 * @author Daniel Korzekwa
 */
trait InferEngine[FROM, TO] {

  def infer(x: FROM): TO
}