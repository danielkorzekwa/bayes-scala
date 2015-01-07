package dk.bayes.infer.epnaivebayes

/**
 * Computes posterior of X for a naive bayes net. Variables: X, Y1|X, Y2|X,...Yn|X
 *
 * @author Daniel Korzekwa
 */
object inferPosterior {

  def apply[X, Y](bn: EPBayesianNet[X, Y]): X = {

    val factorGraph = EPNaiveBayesFactorGraph(bn)
    factorGraph.calibrate(100, 1e-5)
    
    val posterior = factorGraph.getPosterior()
    posterior
  }
}