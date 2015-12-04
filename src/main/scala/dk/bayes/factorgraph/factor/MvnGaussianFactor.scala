package dk.bayes.factorgraph.factor

import dk.bayes.factorgraph.factor.api.SingleFactor
import scala.math._
import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian
import dk.bayes.math.linear.isIdentical
import dk.bayes.factorgraph.factor.api.Factor

case class MvnGaussianFactor(varId: Int, canonGaussian: DenseCanonicalGaussian) extends SingleFactor {

  def getVariableId(): Int = varId

  def marginal(marginalVarId: Int): MvnGaussianFactor = {
    require(marginalVarId == varId, "Incorrect variable id")
    this.copy()
  }

  override def *(factor: Factor): MvnGaussianFactor = {

    factor match {
      case factor: MvnGaussianFactor => {
        require(factor.varId == varId, "Can't multiply two mvn gaussian factors: Factor variable ids do not match")
        MvnGaussianFactor(varId, this.canonGaussian * factor.canonGaussian)
      }
      case _ => throw new IllegalArgumentException("Mvn Gaussian factor cannot be multiplied by a factor that is non mvn gaussian")
    }

  }

  override def /(factor: Factor): MvnGaussianFactor = {

    factor match {
      case factor: MvnGaussianFactor => {

        require(factor.varId == varId, "Can't divide two mvn gaussian factors: Factor variable ids do not match")
        MvnGaussianFactor(varId, this.canonGaussian / factor.canonGaussian)
      }
      case _ => throw new IllegalArgumentException("Mvn Gaussian factor cannot be divided by a factor that is non mvn gaussian")
    }

  }

  override def equals(that: Factor, threshold: Double): Boolean = {

    val thesame = that match {
      case gaussianFactor: MvnGaussianFactor => {
        (isIdentical(canonGaussian.k, gaussianFactor.canonGaussian.k, threshold) &&
          isIdentical(canonGaussian.h, gaussianFactor.canonGaussian.h, threshold))

      }
      case _ => false
    }

    thesame
  }

}