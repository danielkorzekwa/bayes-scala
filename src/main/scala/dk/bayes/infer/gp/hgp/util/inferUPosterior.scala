package dk.bayes.infer.gp.hgp.util

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import dk.bayes.dsl.infer
import dk.bayes.dsl.variable.Gaussian
import dk.gp.cov.CovFunc
import breeze.linalg.cholesky
import breeze.linalg.inv
import dk.gp.math.invchol
import breeze.numerics._

object inferUPosterior {

  /**
   * @param x [cust_id,feature1, feature2,...]
   * @param y
   */
  def apply(x: DenseMatrix[Double], y: DenseVector[Double], u: DenseMatrix[Double], covFunc: CovFunc, covFuncParams: DenseVector[Double], likNoiseLogStdDev: Double): (DenseVector[Double], DenseMatrix[Double]) = {

    val kUU = covFunc.cov(u, u, covFuncParams) + DenseMatrix.eye[Double](u.rows) * 1e-7

    val uMean = DenseVector.zeros[Double](u.rows)
    val uVariable = Gaussian(uMean, kUU)

    val custIds = x(::, 0).toArray.distinct

    val custVariables = custIds.map { cId =>
      val idx = x(::, 0).findAll { x => x == cId }
      val custX = x(idx, ::).toDenseMatrix
      val custY = y(idx).toDenseVector
      val kXX = covFunc.cov(custX, custX, covFuncParams) + DenseMatrix.eye[Double](custX.rows) * 1e-7
      val kXU = covFunc.cov(custX, u, covFuncParams)

      val A = kXU * invchol(cholesky(kUU).t)
      val b = DenseVector.zeros[Double](custX.rows)
      val kXUInvLUU = kXU * inv(cholesky(kUU).t)
      val v = kXX - kXUInvLUU * kXUInvLUU.t + DenseMatrix.eye[Double](custX.rows) * exp(2 * likNoiseLogStdDev)

      val c1Variable = Gaussian(A, uVariable, b, v, yValue = custY)

    }

    val uPosterior = infer(uVariable)
    (uPosterior.m, uPosterior.v)

  }
}