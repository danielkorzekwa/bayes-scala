package dk.bayes.dsl

/**
 * Use this function for inferring the posterior of variable in a Bayesian Network. The algorithm for running the inference is provided by
 * the inferEngine implicit argument
 *
 * @author Daniel Korzekwa
 */
object infer {

  def apply[FROM, TO](x: FROM)(implicit inferEngine: InferEngine[FROM, TO]): TO = {
    inferEngine.infer(x)
  }

}