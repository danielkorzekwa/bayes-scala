package dk.bayes.math.gaussian.canonical

import scala.language.implicitConversions
import scala.collection.JavaConversions._
import scala.math.Pi
import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import breeze.linalg.inv
import breeze.numerics._
import dk.bayes.math.gaussian.Gaussian
import dk.bayes.math.linear.hasUncountable
import dk.bayes.math.linear.filterNot
import dk.bayes.math.linear.filterNotRow
import dk.bayes.math.linear.filterNotColumn
import breeze.linalg.det
import breeze.linalg._
import dk.bayes.math.linear.invchol
import dk.bayes.math.linear.invchol
import dk.bayes.math.linear.invchol

/**
 * Canonical Gaussian following:
 * Kevin P. Murphy, 'A Variational Approximation for Bayesian Networks with Discrete and Continuous Latent Variables'
 * Daphne Koller, Nir Friedman. Probabilistic Graphical Models, Principles and Techniques, 2009'
 *
 * @author Daniel Korzekwa
 *
 * @param varIds List of variable ids over which Canonical Gaussian is defined, for example:  C(X,Y;k,h,g), variables = Array(Xid, Yid)
 * @param k See Canonical Gaussian definition
 * @param h See Canonical Gaussian definition
 * @param g See Canonical Gaussian definition
 */
case class DenseCanonicalGaussian(k: DenseMatrix[Double], h: DenseVector[Double], g: Double) extends CanonicalGaussian with dk.bayes.math.numericops.NumericOps[DenseCanonicalGaussian] {
  private lazy val kinv = {

    if (k.size == 1 && k(0, 0) == 0) DenseMatrix(Double.PositiveInfinity) else invchol(cholesky(k).t)
  }
  lazy val mean = {
    if (!hasUncountable(k)) kinv * h
    else DenseVector(List.fill(h.size)(Double.NaN).toArray)
  }

  lazy val variance = if (!hasUncountable(k)) kinv
  else new DenseMatrix(h.size, h.size, List.fill(h.size * h.size)(Double.NaN).toArray)

  require(k.rows == k.cols && k.rows == h.size, "k and(or) h matrices are incorrect")

  /**
   * Returns the value of probability density function for a given value of x.
   */
  def pdf(x: Double): Double = pdf(DenseVector(x))

  /**
   * Returns the value of probability density function for a given value of vector x.
   */
  def pdf(x: DenseVector[Double]): Double = {
    exp(-0.5 * (x.t * k * x) + h.t * x + g)
  }

  /**
   * Returns gaussian integral marginalising out the variable at a given index
   */
  def marginalise(varIndex: Int): DenseCanonicalGaussian = {

    val kXX = filterNot(k, varIndex, varIndex) //k.filterNot(varIndex, varIndex)
    val kXY = filterNotRow(k(::, varIndex), varIndex) //k.column(varIndex).filterNotRow(varIndex)
    val kYX = filterNotColumn(k(varIndex, ::).t.toDenseMatrix, varIndex) //k.row(varIndex).filterNotColumn(varIndex)
    val hX = filterNotRow(h, varIndex) //h.filterNotRow(varIndex)

    val newK = kXX - kXY * (1d / k(varIndex, varIndex)) * kYX
    val newH = hX - kXY * (1d / k(varIndex, varIndex)) * h(varIndex)
    val newG = g + 0.5 * (log(abs(2 * Pi * (1d / k(varIndex, varIndex)))) + h(varIndex) * (1d / k(varIndex, varIndex)) * h(varIndex))

    DenseCanonicalGaussian(newK, newH, newG)
  }

  /**
   * Marginalise out all variables except of the variable at a given index
   */
  def marginal(varIndex: Int): DenseCanonicalGaussian = {
    val marginalMean = mean(varIndex)
    val marginalVariance = variance(varIndex, varIndex)
    DenseCanonicalGaussian(marginalMean, marginalVariance)
  }

  /**
   * Marginalise out all variables except of the variables at a given indexes
   */
  def marginal(varIndex1: Int, varIndex2: Int): DenseCanonicalGaussian = {

    val marginalMean = DenseVector(mean(varIndex1), mean(varIndex2))

    val v11 = variance(varIndex1, varIndex1)
    val v12 = variance(varIndex1, varIndex2)
    val v21 = variance(varIndex2, varIndex1)
    val v22 = variance(varIndex2, varIndex2)
    val marginalVariance = new DenseMatrix(2, 2, Array(v11, v12, v21, v22))

    val marginal = DenseCanonicalGaussian(marginalMean, marginalVariance)
    marginal
  }

  /**
   * Marginalise out all variables except of the variables at given indexes
   */
  def marginal(varIndexes: Int*): DenseCanonicalGaussian = {

    varIndexes match {
      case Seq(varIndex)             => marginal(varIndex)
      case Seq(varIndex1, varIndex2) => marginal(varIndex1, varIndex2)
      case _ => {
        val filteredVarIndexes = (0 until h.size).filter(v => !varIndexes.contains(v)).reverse
        val headVarIndex = filteredVarIndexes.head
        val marginal = filteredVarIndexes.tail.foldLeft(this.marginalise(headVarIndex))((marginal, nextVarIndex) => marginal.marginalise(nextVarIndex))
        marginal
      }
    }

  }
  /**
   * Returns canonical gaussian given evidence.
   */
  def withEvidence(varIndex: Int, varValue: Double): DenseCanonicalGaussian = {

    val kXY = filterNotRow(k(::, varIndex), varIndex)
    val hX = filterNotRow(h, varIndex)

    val newK = filterNot(k, varIndex, varIndex)
    val newH = hX - kXY * varValue
    val newG = g + h(varIndex) * varValue - 0.5 * varValue * k(varIndex, varIndex) * varValue

    DenseCanonicalGaussian(newK, newH, newG)
  }

  def toGaussian(): Gaussian = {

    require(h.size == 1 && k.size == 1, "Multivariate gaussian cannot be transformed into univariate gaussian")

    val variance = 1d / k(0, 0)
    val mean = variance * h(0)
    Gaussian(mean, variance)
  }

  /**
   * Returns logarithm of normalisation constant.
   */
  def getLogP(): Double = {
    val logP = 0.5 * (mean.t * k * mean) + g
    logP
  }

  /**
   * Extends the scope of Gaussian.
   * It is useful for * and / operations on Gaussians with different variables.
   *
   * @param size The size of extended Gaussian
   * @param startIndex The position of this Gaussian in the new extended Gaussian
   */
  def extend(size: Int, startIndex: Int): DenseCanonicalGaussian = DenseCanonicalGaussianOps.extend(this, size, startIndex)

}

object DenseCanonicalGaussian extends DenseCanonicalGaussianNumericOps {

  /**
   * @param m Mean
   * @param v Variance
   */
  def apply(m: Double, v: Double): DenseCanonicalGaussian = apply(DenseVector(m), DenseMatrix(v))

  /**
   * @param m Mean
   * @param v Variance
   */
  def apply(m: DenseVector[Double], v: DenseMatrix[Double]): DenseCanonicalGaussian = {

    val k = invchol(cholesky(v).t)
    val h = k * m
    val g = -0.5 * (m.t * k * m) - log(pow(2d * Pi, m.size.toDouble / 2d)) - 0.5 * logdet(v)._2

    new DenseCanonicalGaussian(k, h, g)
  }

  /**
   * Creates Canonical Gaussian from Linear Gaussian N(a * x + b, v)
   *
   * @param a Term of m = (ax+b)
   * @param b Term of m = (ax+b)
   * @param v Variance
   */
  def apply(a: DenseMatrix[Double], b: Double, v: Double): DenseCanonicalGaussian = apply(a, DenseVector(b), DenseMatrix(v))

  /**
   * Creates Canonical Gaussian from Linear Gaussian N(a * x + b, v)
   *
   * @param a Term of m = (ax+b)
   * @param b Term of m = (ax+b)
   * @param v Variance
   */
  def apply(a: DenseMatrix[Double], b: DenseVector[Double], v: DenseMatrix[Double]): DenseCanonicalGaussian = {

    val linv = inv(cholesky(v).t)
    val vInv = linv * linv.t
    val atlinv = a.t * linv

    val k00 = atlinv * atlinv.t
    val k01 = (a.t * (-1d)) * vInv
    val k10 = (vInv * (-1d)) * a
    val k11 = vInv

    val k00k01 = DenseMatrix.horzcat(k00, k01)
    val k10k11 = DenseMatrix.horzcat(k10, k11)
    val k = DenseMatrix.vertcat(k00k01, k10k11)

    val h00 = (-1d * a.t) * vInv * b
    val h01 = vInv * b
    val h = DenseVector.vertcat(h00, h01)

    val g = -0.5 * b.t * vInv * b + log(C(v))

    new DenseCanonicalGaussian(k, h, g(0))
  }

  private def C(v: DenseMatrix[Double]): Double = {
    val n = v.size.toDouble
    pow(2 * Pi, -n / 2) * pow(det(v), -0.5)
  }

  implicit def toGaussian(mvnGaussian: DenseCanonicalGaussian): Gaussian = mvnGaussian.toGaussian

}