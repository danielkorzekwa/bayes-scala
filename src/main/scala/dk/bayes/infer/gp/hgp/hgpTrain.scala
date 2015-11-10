package dk.bayes.infer.gp.hgp

import breeze.optimize.ApproximateGradientFunction
import breeze.linalg.DenseVector
import breeze.optimize.LBFGS
import util.calcHgpLoglik

/**
 * Hierarchical Gaussian Process regression. Multiple Gaussian Processes for n tasks with a single shared parent GP.
 */
object hgpTrain {

  def apply(model: HgpModel,maxIter:Int=100): HgpModel = {

    val g = calcLoglik(model)(_)
    val diffFunc = new ApproximateGradientFunction(g)

    val initialParams = DenseVector(model.covFuncParams.toArray :+ model.likNoiseLogStdDev)

    val optimizer = new LBFGS[DenseVector[Double]](maxIter, m = 6, tolerance = 1.0E-6)
    val optIterations = optimizer.iterations(diffFunc, initialParams).toList
    val newParams = optIterations.last.x

    val newCovFuncParams = DenseVector(newParams.toArray.dropRight(1))
    val newNoiseLogStdDev = newParams.toArray.last

    val trainedModel = model.copy(covFuncParams = newCovFuncParams, likNoiseLogStdDev = newNoiseLogStdDev)

    trainedModel
  }

  private def calcLoglik(model: HgpModel)(params: DenseVector[Double]): Double = {

    val currCovFuncParams = DenseVector(params.toArray.dropRight(1))
    val currLikNoiseLogStdDev = params.toArray.last

    val currModel = model.copy(covFuncParams = currCovFuncParams, likNoiseLogStdDev = currLikNoiseLogStdDev)
    val loglik = calcHgpLoglik(currModel)

    -loglik
  }
}