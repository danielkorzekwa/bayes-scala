package dk.bayes.infer.ep.calibrate.fb

/**
 * Expectation Propagation execution summary.
 *
 * @author Daniel Korzekwa
 *
 * @param iterNum The total number of iterations, after which the EP algorithm has reached the convergence criteria
 * @param msgNum The total number of messages sent, after which the EP algorithm has reached the convergence criteria
 */
case class EPSummary(iterNum: Int, msgNum: Long)