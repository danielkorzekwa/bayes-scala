package dk.bayes.learn.lds

/**
 * Latent variable at the time t.
 *
 * @author Daniel Korzekwa
 *
 * @param mean The expected value of the latent variable
 * @param variance The variance value of the latent variable
 * @param covariance The covariance between latent variables at the time t and t-1
 */
case class LatentVariable(mean: Double, variance: Double, covariance: Option[Double])