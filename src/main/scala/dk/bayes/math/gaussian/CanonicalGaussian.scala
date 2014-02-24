package dk.bayes.math.gaussian

import scala.math._
import org.ejml.simple.SimpleMatrix
import org.ejml.simple.SimpleMatrix
import org.ejml.ops.CommonOps
import scala.collection.JavaConversions._
import Linear._

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
case class CanonicalGaussian(k: Matrix, h: Matrix, g: Double) {

  private lazy val kinv = k.inv

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
   * Returns product of multiplying two canonical gausssians
   */
  def *(gaussian: CanonicalGaussian) = if (gaussian.g.isNaN) this else CanonicalGaussianOps.*(this, gaussian)

  /**
   * Returns quotient of two canonical gausssians
   */
  def /(gaussian: CanonicalGaussian) = if (gaussian.g.isNaN()) this else CanonicalGaussianOps./(this, gaussian)

  /**
   * Returns gaussian integral marginalising out the variable at a given index
   */
  def marginalise(varIndex: Int): CanonicalGaussian = {

    val kXX = k.filterNot(varIndex, varIndex)
    val kXY = k.column(varIndex).filterNotRow(varIndex)
    val kYX = k.row(varIndex).filterNotColumn(varIndex)
    val hX = h.filterNotRow(varIndex)

    val newK = kXX - kXY * (1d / k(varIndex, varIndex)) * kYX
    val newH = hX - kXY * (1d / k(varIndex, varIndex)) * h(varIndex)
    val newG = g + 0.5 * (log(abs(2 * Pi * (1d / k(varIndex, varIndex)))) + h(varIndex) * (1d / k(varIndex, varIndex)) * h(varIndex))

    CanonicalGaussian(newK, newH, newG)
  }

  /**
   * Marginalise out all variables except of the variable at a given index
   */
  def marginal(varIndex: Int): CanonicalGaussian = {
    val mean = this.getMean()
    val variance = this.getVariance()
    val marginalMean = mean(varIndex)
    val marginalVariance = variance(varIndex, varIndex)
    CanonicalGaussian(marginalMean, marginalVariance)
  }

  /**
   * Marginalise out all variables except of the variables at given indexes
   */
  def marginal(varIndexes: Int*): CanonicalGaussian = {
    val filteredVarIndexes = (0 until h.size).filter(v => !varIndexes.contains(v)).reverse
    val headVarIndex = filteredVarIndexes.head
    val marginal = filteredVarIndexes.tail.foldLeft(this.marginalise(headVarIndex))((marginal, nextVarIndex) => marginal.marginalise(nextVarIndex))
    marginal
  }
  /**
   * Returns canonical gaussian given evidence.
   */
  def withEvidence(varIndex: Int, varValue: Double): CanonicalGaussian = {

    val kXY = k.column(varIndex).filterNotRow(varIndex)
    val hX = h.filterNotRow(varIndex)

    val newK = k.filterNot(varIndex, varIndex)
    val newH = hX - kXY * varValue
    val newG = g + h(varIndex, 0) * varValue - 0.5 * varValue * k(varIndex, varIndex) * varValue

    CanonicalGaussian(newK, newH, newG)
  }

  def getMean(): Matrix = getMeanAndVariance._1

  def getVariance(): Matrix = getMeanAndVariance._2

  /**
   * @returns (mean,variance)
   */
  def getMeanAndVariance(): Tuple2[Matrix, Matrix] = {

    val mean = if (!g.isNaN) kinv * h
    else Matrix(List.fill(h.size)(Double.NaN).toArray)

    val variance = if (!g.isNaN) kinv
    else Matrix(h.size, h.size, List.fill(h.size * h.size)(Double.NaN).toArray)

    (mean, variance)

  }

  def toGaussian(): Gaussian = {
    val m = getMean()
    val v = getVariance()

    require(m.size == 1 && v.size == 1, "Multivariate gaussian cannot be transformed into univariate gaussian")

    Gaussian(m(0), v(0))
  }

  /**
   * Returns logarithm of normalisation constant.
   */
  def getLogP(): Double = {
    val m = getMean()
    val logP = g + (0.5 * m.transpose * k * m)(0)
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
  def extend(size: Int, startIndex: Int): CanonicalGaussian = CanonicalGaussianOps.extend(this, size, startIndex)
}

object CanonicalGaussian {

  /**
   * @param m Mean
   * @param v Variance
   */
  def apply(m: Double, v: Double): CanonicalGaussian = apply(Matrix(m), Matrix(v))

  /**
   * @param m Mean
   * @param v Variance
   */
  def apply(m: Matrix, v: Matrix): CanonicalGaussian = {
    val k = v.inv
    val h = k * m
    val g = -0.5 * m.transpose * k * m - log(pow(2d * Pi, m.numRows.toDouble / 2d) * pow(v.det, 0.5))
    new CanonicalGaussian(k, h, g(0))
  }

  /**
   * Creates Canonical Gaussian from Linear Gaussian N(a * x + b, v)
   *
   * @param a Term of m = (ax+b)
   * @param b Term of m = (ax+b)
   * @param v Variance
   */
  def apply(a: Matrix, b: Double, v: Double): CanonicalGaussian = {

    val kMatrix = Matrix.zeros(a.numRows() + 1, a.numRows() + 1)

    kMatrix.insertIntoThis(0, 0, a * a.transpose)
    kMatrix.insertIntoThis(0, kMatrix.numCols() - 1, a.negative())
    kMatrix.insertIntoThis(kMatrix.numRows() - 1, 0, a.negative().transpose())
    kMatrix.set(kMatrix.numRows() - 1, kMatrix.numCols() - 1, 1)

    val k = (1d / v) * kMatrix

    val hMatrix = Matrix.zeros(a.numRows() + 1, 1)
    hMatrix.insertIntoThis(0, 0, a.negative)
    hMatrix.set(hMatrix.numRows() - 1, 0, 1)

    val h = (b / v) * hMatrix

    val g = -0.5 * (pow(b, 2) / v) - 0.5 * log(2 * Pi * v)

    new CanonicalGaussian(k, h, g)
  }

  /**
   * Creates Canonical Gaussian from Linear Gaussian N(a * x + b, v)
   *
   * @param a Term of m = (ax+b)
   * @param b Term of m = (ax+b)
   * @param v Variance
   */
  def apply(a: Matrix, b: Matrix, v: Matrix): CanonicalGaussian = {

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

    new CanonicalGaussian(k, h, g(0))
  }

  private def C(v: Matrix): Double = {
    val n = v.size.toDouble
    pow(2 * Pi, -n / 2) * pow(v.det, -0.5)
  }

  implicit def toGaussian(mvnGaussian: CanonicalGaussian): Gaussian = mvnGaussian.toGaussian
}