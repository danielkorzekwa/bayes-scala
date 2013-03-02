package dk.bayes.gaussian

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
case class CanonicalGaussian(varIds: Array[Int], k: Matrix, h: Matrix, g: Double) {

  require(varIds.size == k.numRows, "varIds array doesn't match the dimensions of k matrix")
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
  def *(gaussian: CanonicalGaussian) = CanonicalGaussianMultiply.*(this, gaussian)

  /**
   * Returns gaussian integral marginalising out a given variable
   */
  def marginalise(varId: Int): CanonicalGaussian = {

    val varIndex = varIds.indexOf(varId)

    val newVarIds = varIds.filter(vId => vId != varId)

    val kXX = k.filterNot(varIndex, varIndex)
    val kXY = k.column(varIndex).filterNotRow(varIndex)
    val kYX = k.row(varIndex).filterNotColumn(varIndex)
    val hX = h.filterNotRow(varIndex)

    val newK = kXX - kXY * (1d / k(varIndex, varIndex)) * kYX
    val newH = hX - kXY * (1d / k(varIndex, varIndex)) * h(varIndex)
    val newG = g + 0.5 * (log(abs(2 * Pi * (1d / k(varIndex, varIndex)))) + h(varIndex) * (1d / k(varIndex, varIndex)) * h(varIndex))

    CanonicalGaussian(newVarIds, newK, newH, newG)
  }

  /**
   * Returns canonical gaussian given evidence.
   */
  def withEvidence(varId: Int, varValue: Double): CanonicalGaussian = {

    val varIndex = varIds.indexOf(varId)

    val newVarIds = varIds.filter(vId => vId != varId)

    val kXY = k.column(varIndex).filterNotRow(varIndex)
    val hX = h.filterNotRow(varIndex)

    val newK = k.filterNot(varIndex, varIndex)
    val newH = hX - kXY * varValue
    val newG = g + h(varIndex, 0) * varValue - 0.5 * varValue * k(varIndex, varIndex) * varValue

    CanonicalGaussian(newVarIds, newK, newH, newG)
  }

  def getMean(): Matrix = k.inv * h
  def getVariance(): Matrix = k.inv

  /**
   * Returns logarithm of normalisation constant.
   */
  def getLogP(): Double = {
    val m = getMean()
    val logP = g + (0.5 * m.transpose * k * m)(0)
    logP
  }

  private def exp(m: Matrix) = scala.math.exp(m(0))
}

object CanonicalGaussian {

  /**
   * @param varId Unique variable id
   * @param m Mean
   * @param v Variance
   */
  def apply(varId: Int, m: Double, v: Double): CanonicalGaussian = apply(Array(varId), Matrix(m), Matrix(v))

  /**
   * @param varIds Unique variable ids
   * @param m Mean
   * @param v Variance
   */
  def apply(varIds: Array[Int], m: Matrix, v: Matrix): CanonicalGaussian = {
    val k = v.inv
    val h = k * m
    val g = -0.5 * m.transpose * k * m - log(pow(2d * Pi, m.numRows.toDouble / 2d) * pow(v.det, 0.5))
    new CanonicalGaussian(varIds, k, h, g(0))
  }

  /**
   * Creates Canonical Gaussian from Linear Gaussian N(beta * x + m, v)
   *
   * @param varIds Unique variable ids
   * @param m Mean
   * @param v Variance
   * @beta Slope of linear gaussian function
   */
  def apply(varIds: Array[Int], m: Double, v: Double, beta: Matrix): CanonicalGaussian = {
    val kMatrix = Matrix(beta.numRows() + 1, beta.numRows() + 1)

    kMatrix.insertIntoThis(0, 0, beta * beta.transpose)
    kMatrix.insertIntoThis(0, kMatrix.numCols() - 1, beta.negative())
    kMatrix.insertIntoThis(kMatrix.numRows() - 1, 0, beta.negative().transpose())
    kMatrix.set(kMatrix.numRows() - 1, kMatrix.numCols() - 1, 1)

    val k = (1d / v) * kMatrix

    val hMatrix = Matrix(beta.numRows() + 1, 1)
    hMatrix.insertIntoThis(0, 0, beta.negative)
    hMatrix.set(hMatrix.numRows() - 1, 0, 1)

    val h = (m / v) * hMatrix

    val g = -0.5 * (pow(m, 2) / v) - 0.5 * log(2 * Pi * v)

    new CanonicalGaussian(varIds, k, h, g)
  }

}