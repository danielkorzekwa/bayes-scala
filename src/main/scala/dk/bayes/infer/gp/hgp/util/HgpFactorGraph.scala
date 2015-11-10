package dk.bayes.infer.gp.hgp.util

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import breeze.linalg.cholesky
import dk.bayes.dsl.infer
import dk.bayes.dsl.variable.Gaussian
import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian
import dk.gp.cov.CovFunc
import dk.gp.math.invchol
import breeze.linalg.inv
import breeze.numerics._

case class HgpFactorGraph(x: DenseMatrix[Double], y: DenseVector[Double], u: DenseMatrix[Double], covFunc: CovFunc, covFuncParams: DenseVector[Double], likNoiseLogStdDev: Double) {

  private val kUU = covFunc.cov(u, u, covFuncParams) + DenseMatrix.eye[Double](u.rows) * 1e-7
  private val uMean = DenseVector.zeros[Double](u.rows)
  private val priorU = DenseCanonicalGaussian(uMean, kUU)

  private val xFactorMsgUpByTaskId: Map[Int, DenseCanonicalGaussian] = {

    val taskIds = x(::, 0).toArray.distinct

    val taskMsgsUp = taskIds.par.map { taskId =>
      val idx = x(::, 0).findAll { x => x == taskId }
      val taskX = x(idx, ::).toDenseMatrix
      val taskY = y(idx).toDenseVector
      val kXX = covFunc.cov(taskX, taskX, covFuncParams) + DenseMatrix.eye[Double](taskX.rows) * 1e-7
      val kXU = covFunc.cov(taskX, u, covFuncParams)

      val A = kXU * invchol(cholesky(kUU).t)
      val b = DenseVector.zeros[Double](taskX.rows)
      val kXUInvLUU = kXU * inv(cholesky(kUU).t)
      val v = kXX - kXUInvLUU * kXUInvLUU.t + DenseMatrix.eye[Double](taskX.rows) * exp(2 * likNoiseLogStdDev)

      val priorUVariable = dk.bayes.dsl.variable.Gaussian(priorU.mean, priorU.variance)
      val xVariable = Gaussian(A, priorUVariable, b, v, yValue = taskY)
      val uPosterior = infer(priorUVariable)
      val xMsgUp = DenseCanonicalGaussian(uPosterior.m, uPosterior.v) / priorU

      taskId.toInt -> xMsgUp

    }

    taskMsgsUp.toList.toMap
  }

  def getUFactorMsgDown(): DenseCanonicalGaussian = priorU

  def getXFactorMsgUp(cId: Int): DenseCanonicalGaussian = xFactorMsgUpByTaskId(cId)
  def getXFactorMsgs(): Seq[DenseCanonicalGaussian] = xFactorMsgUpByTaskId.values.toList

  def calcUPosterior(): DenseCanonicalGaussian = getUFactorMsgDown() * getXFactorMsgs.reduceLeft(_ * _)

}
