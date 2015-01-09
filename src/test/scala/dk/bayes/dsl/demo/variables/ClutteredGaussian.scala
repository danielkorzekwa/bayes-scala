package dk.bayes.dsl.demo.variables

import dk.bayes.dsl.variable.gaussian.UnivariateGaussian
import dk.bayes.dsl.Variable
import dk.bayes.dsl.factor.DoubleFactor
import dk.bayes.math.gaussian.LinearGaussian
import dk.bayes.math.gaussian.Proj
import dk.bayes.dsl.variable.gaussian.MultivariateGaussian

/**
 * cluttered_gaussian ~ (1-w)*N(v.m,1) + w*N(0,a)
 */
case class ClutteredGaussian(x: UnivariateGaussian, w: Double, a: Double, value: Double) extends Variable with ClutteredGaussianFactor {
  def getParents(): Seq[Variable] = Vector(x)

}

object ClutteredGaussian {
  
  def apply(x: MultivariateGaussian, xIndex: Int, w: Double, a: Double, value: Double): ClutteredGaussianWithMvnGaussianParent = {
    ClutteredGaussianWithMvnGaussianParent(x, xIndex, w, a, value)
  }
}

trait ClutteredGaussianFactor extends DoubleFactor[UnivariateGaussian, Any] {
  val w: Double
  val a: Double
  val value: Double

  def marginals(x: Option[UnivariateGaussian], y: Option[Any]): (Option[UnivariateGaussian], Option[Any]) = {

    require(x.isDefined, "Not supported")
    require(y.isEmpty, "Not supported")

    val posterior = project(dk.bayes.math.gaussian.Gaussian(x.get.m, x.get.v), w, a, value)
    val posteriorVariable = new UnivariateGaussian(posterior.m, posterior.v)
    (Some(posteriorVariable), None)

  }

 
}