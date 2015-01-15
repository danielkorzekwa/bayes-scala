package dk.bayes.dsl.variable.gaussian

import dk.bayes.math.linear.Matrix
import dk.bayes.dsl.Variable
import dk.bayes.dsl.InferEngine
import dk.bayes.dsl.variable.Gaussian
import dk.bayes.dsl.variable.gaussian.infer.inferMultivariateGaussianSimplest
import dk.bayes.dsl.variable.gaussian.infer.inferMultivariateGaussianEPNaiveBayes
import dk.bayes.dsl.variable.gaussian.infer.inferMultivariateGaussianEPNaiveBayes
import dk.bayes.dsl.variable.gaussian.infer.inferMultivariateGaussianSingleNode

/**
 * N(m,v)
 *
 * @author Daniel Korzekwa
 */
case class MultivariateGaussian(val m: Matrix, val v: Matrix) extends Gaussian {

  def getParents(): Seq[Variable] = Nil
}

object MultivariateGaussian {

  implicit var inferEngines = Vector(
      inferMultivariateGaussianSingleNode,
      inferMultivariateGaussianSimplest,
      inferMultivariateGaussianEPNaiveBayes
      )
}
