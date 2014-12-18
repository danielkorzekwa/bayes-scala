package dk.bayes.model.factor

import scala.math.abs
import dk.bayes.math.gaussian.Gaussian
import dk.bayes.model.factor.api.Factor
import dk.bayes.model.factor.api.SingleFactor

/**
 * This class represents a factor for a Univariate Gaussian Distribution.
 *
 * @author Daniel Korzekwa
 *
 * @param varId Factor variable id
 * @param m Mean
 * @param v Variance
 */
case class GaussianFactor(varId: Int, m: Double, v: Double) extends SingleFactor {

  require(!m.isNaN(), "GaussianFactor mean is NaN")
  require(!v.isNaN(), "GaussianFactor variance is NaN")

  def getVariableId(): Int = varId

  def marginal(marginalVarId: Int): GaussianFactor = {
    require(marginalVarId == varId, "Incorrect variable id")
    this.copy()
  }

  def productMarginal(marginalVarId: Int, factors: Seq[Factor]): GaussianFactor = {
    require(marginalVarId == varId, "Incorrect variable id")
    factors.foldLeft(this)((f1, f2) => f1 * f2)
  }

  override def *(factor: Factor): GaussianFactor = {

    factor match {
      case factor: GaussianFactor => {
        require(factor.varId == varId, "Can't multiply two gaussian factors: Factor variable ids do not match")
        val productGaussian = Gaussian(m, v) * Gaussian(factor.m, factor.v)
        val productFactor = GaussianFactor(varId, productGaussian.m, productGaussian.v)
        productFactor
      }
      case _ => throw new IllegalArgumentException("Gaussian factor cannot be multiplied by a factor that is non gaussian")
    }

  }

  override def /(factor: Factor): GaussianFactor = {

    factor match {
      case factor: GaussianFactor => {

        require(factor.varId == varId, "Can't divide two gaussian factors: Factor variable ids do not match")

        val divideGaussian = Gaussian(m, v) / Gaussian(factor.m, factor.v)

        val divideFactor = GaussianFactor(varId, divideGaussian.m, divideGaussian.v)

        divideFactor
      }
      case _ => throw new IllegalArgumentException("Gaussian factor cannot be divided by a factor that is non gaussian")
    }

  }

  override def equals(that: Factor, threshold: Double): Boolean = {

    val thesame = that match {
      case gaussianFactor: GaussianFactor => {
        (abs(m - gaussianFactor.m) < threshold && (abs(v - gaussianFactor.v) < threshold) || (v.isPosInfinity && gaussianFactor.v.isPosInfinity))
      }
      case _ => false
    }

    thesame
  }

  override def toString() = "GaussianFactor(%d,%.3f,%.3f)".format(varId, m, v)
}