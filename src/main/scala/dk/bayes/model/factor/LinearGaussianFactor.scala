package dk.bayes.model.factor

import dk.bayes.math.gaussian.canonical.CanonicalGaussian
import dk.bayes.math.linear._
import dk.bayes.math.gaussian.LinearGaussian
import dk.bayes.math.gaussian.Gaussian
import dk.bayes.model.factor.api.Factor
import dk.bayes.model.factor.api.DoubleFactor
import dk.bayes.math.gaussian.canonical._

/**
 * This class represents a factor for a Linear Gaussian Distribution. N(ax + b,v)
 *
 * @author Daniel Korzekwa
 *
 * @param parentVarId
 * @param varId
 * @param a Mean term of N(ax + b,v)
 * @param b Mean term of N(ax + b,v)
 * @param v Variance term of N(ax + b,v)
 */
case class LinearGaussianFactor(parentVarId: Int, varId: Int, a: Double, b: Double, v: Double, evidence: Option[Double] = None) extends DoubleFactor {

  def getVariableIds(): Seq[Int] = Vector(parentVarId, varId)

  def marginal(varId: Int): GaussianFactor = GaussianFactor(varId, 0, Double.PositiveInfinity)

  def outgoingMessages(factor1: Factor, factor2: Factor): Tuple2[GaussianFactor, GaussianFactor] = {
    outgoingMessagesInternal(factor1.asInstanceOf[GaussianFactor], factor2.asInstanceOf[GaussianFactor])
  }
  private def outgoingMessagesInternal(parentFactor: GaussianFactor, childFactor: GaussianFactor): Tuple2[GaussianFactor, GaussianFactor] = {

    val parentMsg = evidence match {
      case Some(evidence) => {
        val linearCanonGaussian = DenseCanonicalGaussian(Matrix(a), b, v)
        val msg = (linearCanonGaussian * DenseCanonicalGaussian(childFactor.m, childFactor.v).extend(2, 1)).withEvidence(1, evidence)

        msg.toGaussian
      }
      case _ => {
        if (!childFactor.m.isNaN && !childFactor.v.isPosInfinity) {
          if (a == 1 && b == 0) Gaussian(childFactor.m, childFactor.v + v)
          else {
            val linearCanonGaussian = DenseCanonicalGaussian(Matrix(a), b, v)
            val msg = (linearCanonGaussian * DenseCanonicalGaussian(childFactor.m, childFactor.v).extend(2, 1)).marginalise(varId).toGaussian()
            msg
          }
        } else Gaussian(0, Double.PositiveInfinity)
      }
    }
    val childMsg = evidence match {
      case Some(evidence) => Gaussian(evidence, 0)
      case _ => {
        if (a == 1 && b == 0) Gaussian(parentFactor.m, parentFactor.v + v)
        else (LinearGaussian(a, b, v) * Gaussian(parentFactor.m, parentFactor.v)).marginalise(0).toGaussian()
      }
    }
    Tuple2(GaussianFactor(parentVarId, parentMsg.m, parentMsg.v), GaussianFactor(varId, childMsg.m, childMsg.v))
  }

  override def *(factor: Factor): BivariateGaussianFactor = {

    factor match {
      case factor: GaussianFactor => {
        val gaussianFactor = factor.asInstanceOf[GaussianFactor]
        require(gaussianFactor.varId == parentVarId || gaussianFactor.varId == varId, "Incorrect gaussian variable id")

        val linearCanonGaussian = DenseCanonicalGaussian(Matrix(a), b, v)

        val extendedGaussianFactor = if (gaussianFactor.varId == parentVarId) DenseCanonicalGaussian(gaussianFactor.m, gaussianFactor.v).extend(2, 0)
        else DenseCanonicalGaussian(gaussianFactor.m, gaussianFactor.v).extend(2, 1)

        val productGaussian = linearCanonGaussian * extendedGaussianFactor
        val bivariateGaussianFactor = BivariateGaussianFactor(parentVarId, varId, productGaussian.mean, productGaussian.variance)
        bivariateGaussianFactor
      }
      case _ => throw new IllegalArgumentException("LinearGaussian factor cannot be multiplied by a factor that is non gaussian")
    }
  }

}