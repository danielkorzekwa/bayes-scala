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

    val kXX = calcKxx(varIndex)
    val kXY = calcKxy(varIndex)
    val kYX = calcKyx(varIndex)
    val hX = calcHx(varIndex)

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

    val kXY = calcKxy(varIndex)
    val hX = calcHx(varIndex)

    val newK = calcKxx(varIndex)
    val newH = hX - kXY * varValue
    val newG = g + h(varIndex, 0) * varValue - 0.5 * varValue * k(varIndex, varIndex) * varValue

    CanonicalGaussian(newVarIds, newK, newH, newG)
  }

  def getMu(): Matrix = k.inv * h
  def getSigma(): Matrix = k.inv

  private def calcKxx(yIndex: Int): Matrix = {
    val kValues = for (
      row <- 0 until k.numRows();
      col <- 0 until k.numCols();
      if (row != yIndex && col != yIndex)
    ) yield k(row, col)

    val kXX = Matrix(k.numRows() - 1, k.numCols() - 1, kValues.toArray)
    kXX
  }

  private def calcKxy(yIndex: Int): Matrix = {
    val yColumn = k.column(yIndex)
    val kXYValues = for (row <- 0 until yColumn.numRows(); if (row != yIndex)) yield yColumn(row, 0)
    val kXY = Matrix(yColumn.numRows() - 1, 1, kXYValues.toArray)
    kXY
  }

  private def calcKyx(yIndex: Int): Matrix = {
    val yRow = k.row(yIndex)
    val kYXValues = for (column <- 0 until yRow.numCols(); if (column != yIndex)) yield yRow(0, column)
    val kYX = Matrix(kYXValues.toArray).reshape(1, yRow.numCols() - 1)
    kYX
  }

  private def calcHx(yIndex: Int): Matrix = {
    val hXValues = for (row <- 0 until h.numRows(); if (row != yIndex)) yield h(row, 0)

    val newHx = Matrix(h.numRows() - 1, 1, hXValues.toArray)
    newHx
  }

  private def exp(m: Matrix) = scala.math.exp(m(0))
}

object CanonicalGaussian {

  def apply(varId: Int, mu: Double, sigma: Double): CanonicalGaussian = apply(Array(varId), Matrix(mu), Matrix(sigma))

  def apply(varIds: Array[Int], mu: Matrix, sigma: Matrix): CanonicalGaussian = {
    val k = sigma.inv
    val h = k * mu
    val g = -0.5 * mu.transpose * k * mu - log(pow(2 * Pi, mu.numRows.toDouble / 2) * pow(sigma.det, 0.5))
    new CanonicalGaussian(varIds, k, h, g(0))
  }

  def apply(varIds: Array[Int], mu: Double, sigma: Double, beta: Matrix): CanonicalGaussian = {
    val kMatrix = Matrix(beta.numRows() + 1, beta.numRows() + 1)

    kMatrix.insertIntoThis(0, 0, beta * beta.transpose)
    kMatrix.insertIntoThis(0, kMatrix.numCols() - 1, beta.negative())
    kMatrix.insertIntoThis(kMatrix.numRows() - 1, 0, beta.negative().transpose())
    kMatrix.set(kMatrix.numRows() - 1, kMatrix.numCols() - 1, 1)

    val k = (1d / sigma) * kMatrix

    val hMatrix = Matrix(beta.numRows() + 1, 1)
    hMatrix.insertIntoThis(0, 0, beta.negative)
    hMatrix.set(hMatrix.numRows() - 1, 0, 1)

    val h = (mu / sigma) * hMatrix

    val g = -0.5 * (pow(mu, 2) / sigma) - 0.5 * log(2 * Pi * sigma)

    new CanonicalGaussian(varIds, k, h, g)
  }

}