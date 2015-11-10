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

  case class TaskPosterior(x: DenseMatrix[Double], xPosterior: DenseCanonicalGaussian)

  def apply(xTest: DenseMatrix[Double], model: HgpModel): DenseVector[UnivariateGaussian] = {

    val taskPosteriorByTaskId: Map[Int, TaskPosterior] = createTaskPosteriorByTaskId(xTest, model)

    val predictedArray = (0 until xTest.rows).par.map { rowIndex =>

      val xRow = xTest(rowIndex, ::).t
      val taskId = xRow(0).toInt
      val taskPosterior = taskPosteriorByTaskId(taskId)

      val xTestPrior = inferXPrior(xRow.toDenseMatrix, taskPosterior.x, taskPosterior.xPosterior, model.covFunc, model.covFuncParams, model.likNoiseLogStdDev)
      UnivariateGaussian(xTestPrior._1(0), xTestPrior._2(0, 0))
    }.toArray
    DenseVector(predictedArray)

  }

  private def createTaskPosteriorByTaskId(xTest: DenseMatrix[Double], model: HgpModel): Map[Int, TaskPosterior] = {

    val hgpFactorGraph = HgpFactorGraph(model.x, model.y, model.u, model.covFunc, model.covFuncParams, model.likNoiseLogStdDev)
    val uPosterior = hgpFactorGraph.calcUPosterior()

    val taskIds = xTest(::, 0).toArray.distinct

    val taskPosteriorByTaskId: Map[Int, TaskPosterior] = taskIds.map { taskId =>

      val idx = model.x(::, 0).findAll { x => x == taskId }
      val taskX = model.x(idx, ::).toDenseMatrix
      val taskY = model.y(idx).toDenseVector

      val taskPosterior = if (taskY.size == 0) TaskPosterior(model.u, uPosterior)
      else {
        val taskXTestIdx = xTest(::, 0).findAll(x => x == taskId)
        val taskXTest = xTest(taskXTestIdx, ::).toDenseMatrix

        val taskXX = DenseMatrix.vertcat(taskX, taskXTest)
        val (xPriorMean, cPriorVar) = inferXPrior(taskXX, model.u, uPosterior, model.covFunc, model.covFuncParams, model.likNoiseLogStdDev)

        val xPriorVariable = dk.bayes.dsl.variable.gaussian.multivariate.MultivariateGaussian(xPriorMean, cPriorVar)

        val A = DenseMatrix.horzcat(DenseMatrix.eye[Double](taskX.rows), DenseMatrix.zeros[Double](taskX.rows, taskXTest.rows))
        val yVar = DenseMatrix.eye[Double](taskY.size) * exp(2d * model.likNoiseLogStdDev)
        val yVariable = Gaussian(A, xPriorVariable, b = DenseVector.zeros[Double](taskX.rows), yVar, yValue = taskY) //y variable

        val xPosterior = dk.bayes.dsl.infer(xPriorVariable)
        TaskPosterior(taskXX, DenseCanonicalGaussian(xPosterior.m, xPosterior.v))
      }
      // @TODO Simple impl if xTest is in testX - use it in this situation
      //      val (xPriorMean, cPriorVar) = inferXPrior(testX, model.u, uPosterior, model.covFunc, model.covFuncParams, model.likNoiseLogStdDev)
      //      val xPriorVariable = dk.bayes.dsl.variable.gaussian.multivariate.MultivariateGaussian(xPriorMean, cPriorVar)
      //
      //      val yVar = DenseMatrix.eye[Double](testY.size) * exp(2d * model.likNoiseLogStdDev)
      //      val yVariable = Gaussian(xPriorVariable, yVar, yValue = testY) //y variable
      //
      //      val xPosterior = dk.bayes.dsl.infer(xPriorVariable)
      //
      //      val xTestPrior = inferXPrior(xRow.toDenseMatrix, testX, DenseCanonicalGaussian(xPosterior.m, xPosterior.v), model.covFunc, model.covFuncParams, model.likNoiseLogStdDev)
      //      UnivariateGaussian(xTestPrior._1(0), xTestPrior._2(0, 0))

      taskId.toInt -> taskPosterior
    }.toList.toMap

    taskPosteriorByTaskId
  }

}