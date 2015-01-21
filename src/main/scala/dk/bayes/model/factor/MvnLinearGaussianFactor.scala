package dk.bayes.model.factor

import dk.bayes.math.linear._
import dk.bayes.math.gaussian.LinearGaussian
import dk.bayes.math.gaussian.Gaussian
import dk.bayes.model.factor.api.Factor
import dk.bayes.model.factor.api.DoubleFactor
import dk.bayes.math.linear._
import dk.bayes.model.factor.api.SingleFactor
import dk.bayes.math.linear._
import dk.bayes.math.gaussian.canonical.CanonicalGaussian
import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian

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
case class MvnLinearGaussianFactor(parentVarId: Int, varId: Int, a: Matrix, b: Double, v: Double) extends DoubleFactor {

  require(a.numRows == 1, "Only univariate child is supported")

  def getVariableIds(): Seq[Int] = Vector(parentVarId, varId)

  def marginal(varId: Int): SingleFactor = varId match {
    case `parentVarId` =>
      MvnGaussianFactor(varId, DenseCanonicalGaussian(Matrix(a.size, 1), Matrix(a.size, a.size, (row: Int, col: Int) => Double.PositiveInfinity)))
    case `varId` =>
      GaussianFactor(varId, 0, Double.PositiveInfinity)
  }

  def outgoingMessages(factor1: Factor, factor2: Factor): Tuple2[MvnGaussianFactor, GaussianFactor] = {
    outgoingMessagesInternal(factor1.asInstanceOf[MvnGaussianFactor], factor2.asInstanceOf[GaussianFactor])
  }
  private def outgoingMessagesInternal(parentFactor: MvnGaussianFactor, childFactor: GaussianFactor): Tuple2[MvnGaussianFactor, GaussianFactor] = {

    val linearCanonGaussian = DenseCanonicalGaussian(a, b, v)
    val childFactorCanon = DenseCanonicalGaussian(childFactor.m, childFactor.v)

    val parentMsg = (linearCanonGaussian * childFactorCanon.extend(a.numCols + a.numRows, a.numCols)).marginalise(a.numCols)
    //  val childMsg = CanonicalGaussianOps.*(linearCanonGaussian.varIds, parentFactor.canonGaussian, linearCanonGaussian).marginal(a.size + 1).toGaussian
    //  val childMsgMu = childMsg.m
    //   val childMsgVariance = childMsg.v

    val (parentMean, parentVariance) = (parentFactor.canonGaussian.mean, parentFactor.canonGaussian.variance)
    val childMsgMu = (a * parentMean)(0) + b
    val childMsgVariance = v + (a * parentVariance * a.transpose)(0)
    Tuple2(MvnGaussianFactor(parentVarId, parentMsg), GaussianFactor(varId, childMsgMu, childMsgVariance))
  }

}