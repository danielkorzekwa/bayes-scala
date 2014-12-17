package dk.bayes.dsl.variable.gaussian

import dk.bayes.math.linear.Matrix
import dk.bayes.dsl.Variable

/**
 * N(m,v)
 *
 * @author Daniel Korzekwa
 */
case class MultivariateGaussian(m: Matrix, v: Matrix) extends Variable {

  def getParents(): Seq[Variable] = Nil
}