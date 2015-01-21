package dk.bayes.model.factor

import dk.bayes.model.factor.api.SingleFactor
import dk.bayes.math.linear._
import dk.bayes.model.factor.api.Factor
import dk.bayes.math.gaussian.canonical.CanonicalGaussian
import scala.math._
import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian

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
        (canonGaussian.k.matrix.isIdentical(gaussianFactor.canonGaussian.k.matrix, threshold) &&
          canonGaussian.h.matrix.isIdentical(gaussianFactor.canonGaussian.h.matrix, threshold)) 
          
      }
      case _ => false
    }

    thesame
  }

}