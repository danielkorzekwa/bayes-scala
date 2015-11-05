package dk.bayes.infer.gp.hgp

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import dk.bayes.infer.gp.hgp.util.inferXPrior
import dk.bayes.infer.gp.hgp.util.inferUPosterior
import dk.gp.math.UnivariateGaussian
import dk.bayes.dsl.variable.gaussian.multivariate.MultivariateGaussian
import breeze.numerics._
import breeze.linalg._
import dk.bayes.dsl.variable.Gaussian
import dk.gp.math.UnivariateGaussian
import dk.bayes.infer.gp.hgp.util.inferXPrior

/**
 * Hierarchical Gaussian Process regression. Multiple Gaussian Processes for n tasks with a single shared parent GP.
 */
object hgpPredict {

  def apply(xTest: DenseMatrix[Double], model: HgpModel): DenseVector[UnivariateGaussian] = {

    val (uPosteriorMean, uPosteriorVar) = inferUPosterior(model.x, model.y, model.u, model.covFunc, model.covFuncParams, model.likNoiseLogStdDev)

    val predicted = xTest(*, ::).map { xRow =>

      val cId = xRow(0)

      val idx = model.x(::, 0).findAll { x => x == cId }
      val custX = model.x(idx, ::).toDenseMatrix
      val custY = model.y(idx).toDenseVector

      val (xPriorMean, cPriorVar) = inferXPrior(custX, model.u, MultivariateGaussian(uPosteriorMean, uPosteriorVar), model.covFunc, model.covFuncParams, model.likNoiseLogStdDev)
      val xPriorVariable = MultivariateGaussian(xPriorMean, cPriorVar)

      val yVar = DenseMatrix.eye[Double](custY.size) * exp(2d * model.likNoiseLogStdDev)
      val yVariable = Gaussian(xPriorVariable, yVar, yValue = custY) //y variable

      val xPosterior = dk.bayes.dsl.infer(xPriorVariable)

      val xTestPrior = inferXPrior(xRow.toDenseMatrix, custX, xPosterior, model.covFunc, model.covFuncParams, model.likNoiseLogStdDev)

      //   println("cust=" + cId)
      //  println( DenseMatrix.horzcat(custX,DenseVector.horzcat(xPosterior.m, custY)))

      UnivariateGaussian(xTestPrior._1(0), xTestPrior._2(0, 0))
    }

    predicted
  }
}