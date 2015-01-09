package dk.bayes.dsl

/**
 * Use this function for inferring the posterior of variable in a Bayesian Network. The algorithm for running the inference is provided by
 * the inferEngine implicit argument
 *
 * @author Daniel Korzekwa
 */
object infer {

  def apply[FROM <: Variable, TO <: Variable](x: FROM)(implicit inferEngines: Seq[InferEngine[FROM, TO]] = List[InferEngine[FROM, TO]]()): TO = {

    val inferEngine = inferEngines.find(e => e.isSupported(x))

    val inferredVar = inferEngine match {
      case Some(inferEngine) => inferEngine.infer(x)
      case _ => throw new UnsupportedOperationException("Suitable inference engine not found")
    }

    inferredVar
  }

}