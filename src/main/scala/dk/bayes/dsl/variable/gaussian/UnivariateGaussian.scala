package dk.bayes.dsl.variable.gaussian

import dk.bayes.dsl.Variable
import dk.bayes.dsl.InferEngine

/**
 * N(m,v)
 *
 * @author Daniel Korzekwa
 */
class UnivariateGaussian(val m: Double, val v: Double) extends Variable {

  def getParents(): Seq[Variable] = Nil
}

object UnivariateGaussian {

  implicit val inferEngine = new InferEngine[UnivariateGaussian, UnivariateGaussian] {

    def infer(x: UnivariateGaussian): UnivariateGaussian = {

      /**
       * Supported model: x -> z
       * z - UnivariateLinearGaussian z = x + gaussian noise
       */
      val child = x.getChildren match {
        case Seq(child) if child.isInstanceOf[UnivariateLinearGaussian] => child.asInstanceOf[UnivariateLinearGaussian]
        case _ => throw new UnsupportedOperationException("Inference not supported")
      }
      require(child.getParents().size == 1 && child.getParents()(0).eq(x), "Inference not supported")
      require(!child.hasChildren, "Inference not supported")
      require(child.a == 1 && child.b == 0, "Inference not supported")
      require(child.value.isDefined, "Inference not supported")

      val posteriorVar = 1d / (1d / x.v + 1d / child.v)
      val posteriorMean = posteriorVar * ((1d / child.v) * child.value.get + (1d / x.v) * x.m)
      new UnivariateGaussian(posteriorMean, posteriorVar)
    }
  }

}