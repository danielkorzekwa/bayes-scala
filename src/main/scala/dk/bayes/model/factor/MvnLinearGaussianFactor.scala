package dk.bayes.model.factor

import dk.bayes.math.gaussian.LinearGaussian
import dk.bayes.math.gaussian.Gaussian
import dk.bayes.model.factor.api.Factor
import dk.bayes.model.factor.api.DoubleFactor
import dk.bayes.model.factor.api.SingleFactor
import dk.bayes.math.gaussian.canonical.CanonicalGaussian
import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian
import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector

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
case class MvnLinearGaussianFactor(parentVarId: Int, varId: Int, a: DenseMatrix[Double], b: Double, v: Double) extends DoubleFactor {

  require(a.rows == 1, "Only univariate child is supported")

  def getVariableIds(): Seq[Int] = Vector(parentVarId, varId)

  def marginal(varId: Int): SingleFactor = varId match {
    case `parentVarId` =>
      MvnGaussianFactor(varId, DenseCanonicalGaussian(DenseVector.zeros[Double](a.size),  DenseMatrix.eye[Double](a.size)*100d ))
    case `varId` =>
      GaussianFactor(varId, 0, Double.PositiveInfinity)
  }

  def outgoingMessages(factor1: Factor, factor2: Factor): Tuple2[MvnGaussianFactor, GaussianFactor] = {
    outgoingMessagesInternal(factor1.asInstanceOf[MvnGaussianFactor], factor2.asInstanceOf[GaussianFactor])
  }
  private def outgoingMessagesInternal(parentFactor: MvnGaussianFactor, childFactor: GaussianFactor): Tuple2[MvnGaussianFactor, GaussianFactor] = {

    val linearCanonGaussian = DenseCanonicalGaussian(a, b, v)
    val childFactorCanon = DenseCanonicalGaussian(childFactor.m, childFactor.v)

    val parentMsg = (linearCanonGaussian * childFactorCanon.extend(a.cols + a.rows, a.cols)).marginalise(a.cols)
    //  val childMsg = CanonicalGaussianOps.*(linearCanonGaussian.varIds, parentFactor.canonGaussian, linearCanonGaussian).marginal(a.size + 1).toGaussian
    //  val childMsgMu = childMsg.m
    //   val childMsgVariance = childMsg.v

    val (parentMean, parentVariance) = (parentFactor.canonGaussian.mean, parentFactor.canonGaussian.variance)
    val childMsgMu = (a * parentMean) + b
    val childMsgVariance = a * parentVariance * a.t + v
    Tuple2(MvnGaussianFactor(parentVarId, parentMsg), GaussianFactor(varId, childMsgMu(0), childMsgVariance(0,0)))
  }

}