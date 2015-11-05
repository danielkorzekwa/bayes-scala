package dk.bayes.infer.gp.hgp

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import dk.gp.cov.CovFunc

/**
 * @param x [taskId, feature1, feature2, ...]
 * @param y
 * @param u Inducing variables for a parent GP [taskId, feature1, feature2, ...]
 * @param covFunc
 * @param covFuncParams
 * @param likNoiseLogStdDev
 */
case class HgpModel(x: DenseMatrix[Double], y: DenseVector[Double], u: DenseMatrix[Double], covFunc: CovFunc, covFuncParams: DenseVector[Double], likNoiseLogStdDev: Double)