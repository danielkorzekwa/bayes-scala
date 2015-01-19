package dk.bayes.infer.epnaivebayes

import dk.bayes.dsl.factor.SingleFactor
import dk.bayes.dsl.factor.DoubleFactor
import dk.bayes.math.numericops._

/**
 * Computes posterior of X for a naive bayes net. Variables: X, Y1|X, Y2|X,...Yn|X
 *
 * @param prior
 *  @param likelihoods
 * @param paralllelMessagePassing If true then messages between X variable and Y variables are sent in parallel
 *
 * @author Daniel Korzekwa
 */
object inferPosterior {

  
  def apply[X, Y](prior: SingleFactor[X], likelihoods: Seq[DoubleFactor[X,_]], paralllelMessagePassing: Boolean = false)
  (implicit multOp: multOp[X, X], divideOp: divideOp[X, X], isIdentical: isIdentical[X, X]): X = {

    val factorGraph = EPNaiveBayesFactorGraph(prior,likelihoods, paralllelMessagePassing)
    factorGraph.calibrate(100, 1e-5)

    val posterior = factorGraph.getPosterior()
    posterior
  }
}