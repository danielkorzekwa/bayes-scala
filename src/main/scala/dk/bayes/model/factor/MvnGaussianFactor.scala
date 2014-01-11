package dk.bayes.model.factor

import dk.bayes.model.factor.api.SingleFactor
import dk.bayes.math.gaussian.Linear.Matrix
import dk.bayes.model.factor.api.Factor
import dk.bayes.math.gaussian.CanonicalGaussian
import scala.math._

case class MvnGaussianFactor(varId: Int, mean: Matrix, variance: Matrix) extends SingleFactor {

  def getVariableId(): Int = varId

  def marginal(marginalVarId: Int): MvnGaussianFactor = {
    require(marginalVarId == varId, "Incorrect variable id")
    this.copy()
  }

  override def /(factor: Factor): MvnGaussianFactor = {

    factor match {
      case factor: MvnGaussianFactor => {

        require(factor.varId == varId, "Can't divide two mvn gaussian factors: Factor variable ids do not match")
        require(mean.size == factor.mean.size, "Can't divide gaussians of different dimensions")
        require(variance.size == factor.variance.size, "Can't divide gaussians of different dimensions")

        val divideGaussian = CanonicalGaussian((1 to mean.size).toArray, mean, variance) / CanonicalGaussian((1 to mean.size).toArray, factor.mean, factor.variance)

        val divideFactor = MvnGaussianFactor(varId, divideGaussian.getMean(), divideGaussian.getVariance())

        divideFactor
      }
      case _ => throw new IllegalArgumentException("Mvn Gaussian factor cannot be divided by a factor that is non mvn gaussian")
    }

  }
  
  override def equals(that: Factor, threshold: Double): Boolean = {

    val thesame = that match {
      case gaussianFactor: MvnGaussianFactor => {
        ((mean - gaussianFactor.mean).matrix.elementMaxAbs() < threshold && ((variance - gaussianFactor.variance).matrix.elementMaxAbs() < threshold))
      }
      case _ => false
    }

    thesame
  }

}