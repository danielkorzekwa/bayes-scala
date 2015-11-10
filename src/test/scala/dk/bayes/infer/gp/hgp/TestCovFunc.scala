package dk.bayes.infer.gp.hgp

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import dk.gp.cov.CovSEiso
import dk.gp.cov.CovFunc
import breeze.numerics._

case class TestCovFunc() extends CovFunc {

  def cov(x1: DenseMatrix[Double], x2: DenseMatrix[Double], covFuncParams: DenseVector[Double]): DenseMatrix[Double] = {

    val (cust1Vec, cust2Vec) = if (x1(0, 0) == x2(0, 0)) (DenseVector.zeros[Double](x1.rows), DenseVector.zeros[Double](x2.rows))
    else (DenseVector.zeros[Double](x1.rows), DenseVector.ones[Double](x2.rows))

    val accountIdCov = CovSEiso().cov(cust1Vec.toDenseMatrix.t, cust2Vec.toDenseMatrix.t, DenseVector(log(1e-3), log(1e-10)))

    accountIdCov + CovSEiso().cov(x1(::, 1 to 1), x2(::, 1 to 1), covFuncParams)
  }

  def covD(x1: DenseMatrix[Double], x2: DenseMatrix[Double], covFuncParams: DenseVector[Double]): Array[DenseMatrix[Double]] = CovSEiso().covD(x1(::, 1 to 1), x2(::, 1 to 1), covFuncParams)

}