package dk.bayes.infer.gp.hgp.util

import breeze.linalg.DenseMatrix
import dk.gp.gpr.gprLoglik
import breeze.linalg.cholesky
import dk.gp.math.invchol
import breeze.numerics._
import dk.bayes.infer.gp.hgp.HgpModel
import dk.gp.math.MultivariateGaussian
import dk.gp.math.MultivariateGaussian

object calcHgpLoglik {

  def apply(model: HgpModel): Double = {

    val hgpFactorGraph = HgpFactorGraph(model.x, model.y, model.u, model.covFunc, model.covFuncParams, model.likNoiseLogStdDev)
    val uPosterior = hgpFactorGraph.calcUPosterior()

    val custIds = model.x(::, 0).toArray.distinct

    val logliks = custIds.map { cId =>

      val idx = model.x(::, 0).findAll { x => x == cId }
      val custX = model.x(idx, ::).toDenseMatrix
      val custY = model.y(idx).toDenseVector

      val xFactorMsgUp = hgpFactorGraph.getXFactorMsgUp(cId.toInt)

      val uVarMsgDown = uPosterior / xFactorMsgUp
      val (xPriorMean, cPriorVar) = inferXPrior(custX, model.u, uVarMsgDown, model.covFunc, model.covFuncParams, model.likNoiseLogStdDev)
      val cPriorVarWithNoise = cPriorVar + DenseMatrix.eye[Double](custX.rows) * exp(2 * model.likNoiseLogStdDev)
      val loglik = gprLoglik(xPriorMean, cPriorVarWithNoise, invchol(cholesky(cPriorVarWithNoise).t), custY)

      loglik
    }

    val totalLoglik = logliks.sum

    totalLoglik
  }
}