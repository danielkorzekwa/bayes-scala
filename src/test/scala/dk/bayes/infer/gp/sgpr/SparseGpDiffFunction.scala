package dk.bayes.infer.gp.sgpr

import breeze.optimize.DiffFunction
import breeze.linalg.DenseVector
import dk.bayes.math.linear._
import breeze.linalg._
import dk.bayes.infer.gp.cov.CovSEiso
import scala.math._
import dk.bayes.math.linear.Matrix
import scala.language.implicitConversions._
import scala.language.implicitConversions

case class SparseGpDiffFunction(x: Matrix, y: Matrix, u: Matrix) extends DiffFunction[DenseVector[Double]] {

  /**
   * @param x Logarithm of [signal standard deviation,length-scale,likelihood noise standard deviation]
   */
  def calculate(params: DenseVector[Double]): (Double, DenseVector[Double]) = {
    val (sf, ell, likStdDev) = (params(0), params(1), params(2))

    val covFunc = CovSEiso(sf, ell)
    val gp = GenericSparseGPR(x, y, u, covFunc, likStdDev)

    val kNNDiagDArray = calcKnnDiagDArray(covFunc)
    val kMMdArray = covFunc.covD(u).map(x => toDenseMatrix(x))
    val kNMdArray = covFunc.covNMd(x, u).map(x => toDenseMatrix(x))

    val (loglik, loglikDKernel, loglikDLikNoise) = gp.loglikWithD(kMMdArray, kNMdArray, kNNDiagDArray)
    val negativeD = DenseVector(loglikDKernel.map(x => -x) :+ (-loglikDLikNoise))
    (-loglik, negativeD)
  }

  private implicit def toDenseMatrix(m: Matrix): DenseMatrix[Double] = {
    DenseMatrix(m.t.toArray).reshape(m.numRows, m.numCols)
  }

  private def calcKnnDiagDArray(covFunc: CovSEiso): Array[DenseVector[Double]] = {
    val kNNDiagDArrayOfArrays = (0 until x.numRows()).map(rowIndex => covFunc.covD(x.extractRow(rowIndex))).toArray

    val kNNDiagDArray = (0 until kNNDiagDArrayOfArrays(0).size).map { dIndex =>
      DenseVector((0 until x.numRows()).map(rowIndex => kNNDiagDArrayOfArrays(rowIndex)(dIndex).at(0)).toArray)
    }.toArray

    kNNDiagDArray
  }

}