package dk.bayes.learn.em
import dk.bayes.model.clustergraph.ClusterGraph
import dk.bayes.learn.em.EMLearn._

/**
 * Learns parameters of Bayesian Network with Expectation Maximisation algorithm, presented in
 * 'Daphne Koller, Nir Friedman. Probabilistic Graphical Models, Principles and Techniques, 2009' book.
 *
 * @author Daniel Korzekwa
 */
trait EMLearn {

  /**
   * Learns parameters of Bayesian Network with Expectation Maximisation algorithm.
   *
   *  @param clusterGraph Cluster graph, which parameters are learned for
   *
   *  EM learning expects this cluster graph to contain initial cluster potentials in a form of table CPTs.
   *  For instance, for a cluster initial potentials: Factor(winterVar, sprinklerVar, Array(0.6, 0.4, 0.55, 0.45)),
   *  all variables except the last one (sprinkler) act as conditioning variables.
   *
   *  At the end of learning process, cluster graph is updated  with the latest learned cluster potentials.
   *  While learning parameters, cluster graph is used for performing inference during expectation step of EM algorithm.
   *
   *  To inform EM algorithm about shared cluster initial potentials, for instance while learning unrolled Dynamic Bayesian Network,
   *  use clusterTypeId field on a Cluster object.
   *
   *  @param trainSet Data set used for learning parameters of Bayesian Network
   *
   *  @param maxIterNum Maximum number of iterations for which EM algorithm is executed
   *
   *  @param progress Progress monitoring. It is called by this method at the end of every iteration
   */
  def learn(clusterGraph: ClusterGraph, trainSet: DataSet, maxIterNum: Int, progress: (Progress) => Unit)
}

object EMLearn {

  /**
   * Represents the current progress of EM learning.
   *
   * @param iterNum The current iteration number of EM learning
   * @param Complete data log likelihood
   */
  case class Progress(iterNum: Int, logLikelihood: Double)
}

