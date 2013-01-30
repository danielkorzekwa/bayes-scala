package dk.bayes.gaussian

/**
 * Linear Gaussian Model: N(mu,sigma), where,
 * mu = ax + b
 *
 * @author Daniel Korzekwa
 *
 * @param a Mean component: mu = ax + b
 * @param b Mean component: mu = ax + b
 * @param sigma Variance
 */
case class LinearGaussian(a: Double, b: Double, sigma: Double)