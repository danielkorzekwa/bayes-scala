package dk.bayes.dsl.variable.gaussian

import dk.bayes.dsl.Variable

/**
 * y = A*x + b + gaussian_noise
 *
 * @author Daniel Korzekwa
 */
class UnivariateLinearGaussian(val a: Double, val x:UnivariateGaussian,val b: Double, val v: Double, val value: Option[Double] = None) extends Variable{

   def getParents(): Seq[Variable] = List(x)
}