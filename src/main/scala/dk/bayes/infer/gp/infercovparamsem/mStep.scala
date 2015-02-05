package dk.bayes.infer.gp.infercovparamsem

import breeze.linalg.DenseVector
import dk.bayes.math.gaussian.MultivariateGaussian
import breeze.optimize.DiffFunction
import dk.bayes.math.linear.Matrix
import breeze.optimize.LBFGS
import scala.math._
import breeze.linalg.logdet
import breeze.linalg.DenseMatrix

/**
 * Returns new values of f's covariance parameters, that maximise the value of variational lower bound
 * (only the expected complete data log likelihood term is maximised, as the entropy term of variational lower bound does not depend on f's covariance parameters,
 * see Bishop's book chapter 9.4 The EM algorithm in general)
 *
 * @author Daniel Korzekwa
 */
object mStep {

  /**
   * @param initialParams
   * @param calcFPriorVar (params) => f prior variance
   * @param calcFPriorVarD (params => derivatives of f prior variance with respect to hyper parameters
   */
  def apply(fPosterior: MultivariateGaussian, initialParams: Array[Double],
            calcFPriorVar: (Array[Double]) => Matrix, calcFPriorVarD: (Array[Double]) => Array[Matrix]): Array[Double] = {
    val initialParamsVec = DenseVector(initialParams)

    val diffFunction = GenericDiffFunction(fPosterior, calcFPriorVar, calcFPriorVarD)

    val optimizer = new LBFGS[DenseVector[Double]](maxIter = 100, m = 3, tolerance = 1.0E-6)
    val optIterations = optimizer.iterations(diffFunction, initialParamsVec).toList

    val newParams = optIterations.last.x

    newParams.data
  }

  case class GenericDiffFunction(fPosterior: MultivariateGaussian, calcFPriorVar: (Array[Double]) => Matrix, calcFPriorVarD: (Array[Double]) => Array[Matrix]) extends DiffFunction[DenseVector[Double]] {

    /**
     * @param x Logarithm of [signal standard deviation,length-scale,likelihood noise standard deviation]
     */
    def calculate(params: DenseVector[Double]): (Double, DenseVector[Double]) = {

      val fPriorVar = calcFPriorVar(params.data)
      val fPriorVarInv = fPriorVar.inv
      val fPriorVarD = calcFPriorVarD(params.data)

      val f = -loglik(fPriorVar, fPriorVarInv)
      
      //calculate partial derivatives
      val df = fPriorVarD.map(varD => -loglikD(fPriorVarInv, varD)).toArray

      (f, DenseVector(df))
    }

    /**
     * (http://mlg.eng.cam.ac.uk/zoubin/papers/ecml03.pdf, http://mlg.eng.cam.ac.uk/zoubin/papers/KimGha06-PAMI.pdf)
     */
    private def loglik(fPriorVar: Matrix, fPriorVarInv: Matrix): Double = {
      val logDet = logdet(new DenseMatrix(fPriorVar.numRows(),fPriorVar.numRows(),fPriorVar.toArray()))._2
      -0.5 * logDet - 0.5 * (fPosterior.m.t * fPriorVar.inv * fPosterior.m)(0) - 0.5 * (fPriorVarInv * fPosterior.v).trace
    }

    /**
     * @param covD element-wise matrix derivatives
     * (http://mlg.eng.cam.ac.uk/zoubin/papers/ecml03.pdf, http://mlg.eng.cam.ac.uk/zoubin/papers/KimGha06-PAMI.pdf)
     *
     */
    private def loglikD(fPriorVarInv: Matrix, covD: Matrix): Double =
      -0.5 * (fPriorVarInv * covD).trace + 0.5 * (fPosterior.m.t * fPriorVarInv * covD * fPriorVarInv * fPosterior.m)(0) +
        0.5 * (fPriorVarInv * covD * fPriorVarInv * fPosterior.v).trace
  }
}