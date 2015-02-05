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

  
  def apply[X, Y](prior: SingleFactor[X], likelihoods: Seq[DoubleFactor[X,_]], paralllelMessagePassing: Boolean = false,maxIter: Int = 100, threshold: Double = 1e-6)
  (implicit multOp: multOp[X], divideOp: divideOp[X], isIdentical: isIdentical[X]): X = {

    val factorGraph = EPNaiveBayesFactorGraph(prior,likelihoods, paralllelMessagePassing)
    factorGraph.calibrate(maxIter,threshold)

    val posterior = factorGraph.getPosterior()
    posterior
  }
}