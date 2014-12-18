package dk.bayes.dsl.variable.gaussian

import dk.bayes.math.linear.Matrix
import dk.bayes.dsl.Variable
import dk.bayes.dsl.InferEngine

/**
 * y = A*x + b + gaussian_noise
 *
 * @author Daniel Korzekwa
 */
class MultivariateLinearGaussian(val a: Matrix, val x: MultivariateGaussian, val b: Matrix, val v: Matrix) extends Variable {

  def getParents(): Seq[Variable] = List(x)
}

object MultivariateLinearGaussian {

  implicit val inferEngine = new InferEngine[MultivariateLinearGaussian, MultivariateGaussian] {

    def infer(v: MultivariateLinearGaussian): MultivariateGaussian = {

      /**
       * Supported model: x -> y
       * x - MultivariateGaussian
       */
      require(!v.x.hasParents, "Inference not supported")
      val xx = v.x.getChildren match {
        case Seq(v) =>
        case _ => throw new UnsupportedOperationException("Inference not supported")
      }
      require(!v.hasChildren, "Inference not supported")

      /**
       * Run the inference
       */
      
      val x = v.x

      val skillMean = v.a * x.m + v.b
      val skillVar = v.v + v.a * x.v * v.a.t

      MultivariateGaussian(skillMean, skillVar)
    }
  }
}