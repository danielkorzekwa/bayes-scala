package dk.bayes.math.gaussian.canonical

import scala.math._
import org.ejml.simple.SimpleMatrix
import org.ejml.simple.SimpleMatrix
import org.ejml.ops.CommonOps
import scala.collection.JavaConversions._
import dk.bayes.math.linear._
import dk.bayes.math.numericops._
import dk.bayes.math.gaussian.Gaussian

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
case class DenseCanonicalGaussian(k: Matrix, h: Matrix, g: Double) extends CanonicalGaussian with NumericOps[DenseCanonicalGaussian] {
  private lazy val kinv = k.inv

  lazy val mean = {
    if (!k.matrix.hasUncountable()) kinv * h
    else Matrix(List.fill(h.size)(Double.NaN).toArray)
  }

  lazy val variance = if (!k.matrix.hasUncountable()) kinv
  else Matrix(h.size, h.size, List.fill(h.size * h.size)(Double.NaN).toArray)

  require(k.numRows == k.numCols && k.numRows == h.numRows && h.numCols == 1, "k and(or) h matrices are incorrect")

  /**
   * Returns the value of probability density function for a given value of x.
   */
  def pdf(x: Double): Double = pdf(Matrix(x))

  /**
   * Returns the value of probability density function for a given value of vector x.
   */
  def pdf(x: Matrix): Double = exp(-0.5 * x.transpose * k * x + h.transpose * x + g)

  /**
   * Returns gaussian integral marginalising out the variable at a given index
   */
  def marginalise(varIndex: Int): DenseCanonicalGaussian = {

    val kXX = k.filterNot(varIndex, varIndex)
    val kXY = k.column(varIndex).filterNotRow(varIndex)
    val kYX = k.row(varIndex).filterNotColumn(varIndex)
    val hX = h.filterNotRow(varIndex)

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

    val marginalMean = Matrix(mean(varIndex1), mean(varIndex2))

    val v11 = variance(varIndex1, varIndex1)
    val v12 = variance(varIndex1, varIndex2)
    val v21 = variance(varIndex2, varIndex1)
    val v22 = variance(varIndex2, varIndex2)
    val marginalVariance = Matrix(2, 2, Array(v11, v12, v21, v22))

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

    val kXY = k.column(varIndex).filterNotRow(varIndex)
    val hX = h.filterNotRow(varIndex)

    val newK = k.filterNot(varIndex, varIndex)
    val newH = hX - kXY * varValue
    val newG = g + h(varIndex, 0) * varValue - 0.5 * varValue * k(varIndex, varIndex) * varValue

    DenseCanonicalGaussian(newK, newH, newG)
  }

  def toGaussian(): Gaussian = {

    require(h.size == 1 && k.size == 1, "Multivariate gaussian cannot be transformed into univariate gaussian")

    val variance = 1d / k(0)
    val mean = variance * h(0)
    Gaussian(mean, variance)
  }

  /**
   * Returns logarithm of normalisation constant.
   */
  def getLogP(): Double = {
    val logP = g + (0.5 * mean.transpose * k * mean)(0)
    logP
  }

  private def exp(m: Matrix) = scala.math.exp(m(0))

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
  def apply(m: Double, v: Double): DenseCanonicalGaussian = apply(Matrix(m), Matrix(v))

  /**
   * @param m Mean
   * @param v Variance
   */
  def apply(m: Matrix, v: Matrix): DenseCanonicalGaussian = {
    val k = v.inv
    val h = k * m
    val g = -0.5 * m.transpose * k * m - log(pow(2d * Pi, m.numRows.toDouble / 2d) * pow(v.det, 0.5))
    new DenseCanonicalGaussian(k, h, g(0))
  }

  /**
   * Creates Canonical Gaussian from Linear Gaussian N(a * x + b, v)
   *
   * @param a Term of m = (ax+b)
   * @param b Term of m = (ax+b)
   * @param v Variance
   */
  def apply(a: Matrix, b: Double, v: Double): DenseCanonicalGaussian = apply(a, Matrix(b), Matrix(v))

  /**
   * Creates Canonical Gaussian from Linear Gaussian N(a * x + b, v)
   *
   * @param a Term of m = (ax+b)
   * @param b Term of m = (ax+b)
   * @param v Variance
   */
  def apply(a: Matrix, b: Matrix, v: Matrix): DenseCanonicalGaussian = {

    val vInv = v.inv

    val k00 = a.transpose * vInv * a
    val k01 = a.transpose.negative * vInv
    val k10 = vInv.negative * a
    val k11 = vInv

    var k = k00.combine(0, k00.numCols, k01)
    k = k.combine(k00.numRows, 0, k10)
    k = k.combine(k00.numRows, k00.numCols, k11)

    val h00 = a.transpose.negative * vInv * b
    val h01 = vInv * b
    val h = h00.combine(h00.numRows, 0, h01)

    val g = -0.5 * b.transpose * v.inv * b + log(C(v))

    new DenseCanonicalGaussian(k, h, g(0))
  }

  private def C(v: Matrix): Double = {
    val n = v.size.toDouble
    pow(2 * Pi, -n / 2) * pow(v.det, -0.5)
  }

  implicit def toGaussian(mvnGaussian: DenseCanonicalGaussian): Gaussian = mvnGaussian.toGaussian

}