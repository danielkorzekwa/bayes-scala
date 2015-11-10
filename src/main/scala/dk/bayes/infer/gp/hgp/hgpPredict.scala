package dk.bayes.infer.gp.hgp

import breeze.linalg._
import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import breeze.numerics._
import dk.bayes.dsl.variable.Gaussian
import dk.bayes.infer.gp.hgp.util.HgpFactorGraph
import dk.bayes.infer.gp.hgp.util.inferXPrior
import dk.bayes.infer.gp.hgp.util.inferXPrior
import dk.gp.math.MultivariateGaussian
import dk.gp.math.MultivariateGaussian
import dk.gp.math.UnivariateGaussian
import dk.gp.math.UnivariateGaussian
import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian

/**
 * Hierarchical Gaussian Process regression. Multiple Gaussian Processes for n tasks with a single shared parent GP.
 */
object hgpPredict {

  def apply(xTest: DenseMatrix[Double], model: HgpModel): DenseVector[UnivariateGaussian] = {

    val hgpFactorGraph = HgpFactorGraph(model.x, model.y, model.u, model.covFunc, model.covFuncParams, model.likNoiseLogStdDev)
    val uPosterior = hgpFactorGraph.calcUPosterior()

    val predicted = xTest(*, ::).map { xRow =>

      val cId = xRow(0)

      val idx = model.x(::, 0).findAll { x => x == cId }
      val custX = model.x(idx, ::).toDenseMatrix
      val custY = model.y(idx).toDenseVector

      val predicted = if (custY.size == 0) {

        val (xPriorMean, cPriorVar) = inferXPrior(xRow.toDenseMatrix, model.u, uPosterior, model.covFunc, model.covFuncParams, model.likNoiseLogStdDev)
        UnivariateGaussian(xPriorMean(0), cPriorVar(0, 0))

      } else {

        val custXX = DenseMatrix.vertcat(custX, xRow.toDenseMatrix)

        val (xPriorMean, cPriorVar) = inferXPrior(custXX, model.u, uPosterior, model.covFunc, model.covFuncParams, model.likNoiseLogStdDev)

        val xPriorVariable = dk.bayes.dsl.variable.gaussian.multivariate.MultivariateGaussian(xPriorMean, cPriorVar)

        val A = DenseMatrix.horzcat(DenseMatrix.eye[Double](custX.rows), DenseMatrix.zeros[Double](custX.rows, 1))
        val yVar = DenseMatrix.eye[Double](custY.size) * exp(2d * model.likNoiseLogStdDev)
        val yVariable = Gaussian(A, xPriorVariable, b = DenseVector.zeros[Double](custX.rows), yVar, yValue = custY) //y variable

        val xPosterior = dk.bayes.dsl.infer(xPriorVariable)

        val xTestPrior = inferXPrior(xRow.toDenseMatrix, custXX, DenseCanonicalGaussian(xPosterior.m, xPosterior.v), model.covFunc, model.covFuncParams, model.likNoiseLogStdDev)
        UnivariateGaussian(xTestPrior._1(0), xTestPrior._2(0, 0))
      }
      // @TODO Simple impl if xTest is in custX - use it in this situation
      //      val (xPriorMean, cPriorVar) = inferXPrior(custX, model.u, uPosterior, model.covFunc, model.covFuncParams, model.likNoiseLogStdDev)
      //      val xPriorVariable = dk.bayes.dsl.variable.gaussian.multivariate.MultivariateGaussian(xPriorMean, cPriorVar)
      //
      //      val yVar = DenseMatrix.eye[Double](custY.size) * exp(2d * model.likNoiseLogStdDev)
      //      val yVariable = Gaussian(xPriorVariable, yVar, yValue = custY) //y variable
      //
      //      val xPosterior = dk.bayes.dsl.infer(xPriorVariable)
      //
      //      val xTestPrior = inferXPrior(xRow.toDenseMatrix, custX, DenseCanonicalGaussian(xPosterior.m, xPosterior.v), model.covFunc, model.covFuncParams, model.likNoiseLogStdDev)
      //      UnivariateGaussian(xTestPrior._1(0), xTestPrior._2(0, 0))

      predicted

    }

    predicted
  }
}