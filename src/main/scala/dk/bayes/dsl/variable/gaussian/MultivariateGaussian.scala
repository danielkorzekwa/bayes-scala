package dk.bayes.dsl.variable.gaussian

import dk.bayes.math.linear.Matrix
import dk.bayes.dsl.Variable
import dk.bayes.dsl.InferEngine
import dk.bayes.dsl.variable.Gaussian

/**
 * N(m,v)
 *
 * @author Daniel Korzekwa
 */
class MultivariateGaussian(val m: Matrix, val v: Matrix) extends Gaussian {

  def getParents(): Seq[Variable] = Nil
}
