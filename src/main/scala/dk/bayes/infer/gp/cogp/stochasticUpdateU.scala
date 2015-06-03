package dk.bayes.infer.gp.cogp

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import breeze.linalg.inv

/**
 * Stochastic update for the parameters (mu,S) of q(u|y)
 *
 * Nguyen et al. Collaborative Multi-output Gaussian Processes, 2014
 */
object stochasticUpdateU {

  /**
   * @param j Index of Q variable
   * @param beta [P x 1]
   * @param w [P x Q]
   * @param mQ [Z x Q]
   * @param mP [Z x P]
   * @param S [Z x Z]
   * @param kZZ [Z x Z]
   * @param kXZ [X x Z]
   * @param y [X x P]
   * @param l Learning rate
   */
  def apply(j: Int, beta: DenseVector[Double], w: DenseMatrix[Double], mQ: Array[DenseVector[Double]], mP: Array[DenseVector[Double]], S: DenseMatrix[Double],
            kZZ: DenseMatrix[Double], kXZ: DenseMatrix[Double], y: DenseMatrix[Double], l: Double): Tuple2[DenseVector[Double], DenseMatrix[Double]] = {

    //natural parameters theta
    val theta1 = inv(S) * mQ(j)
    val theta2 = -0.5 * inv(S)

    /**
     * Natural gradient with respect to natural parameter is just a standard gradient with respect to expectation parameters.
     * Thus no need for inverse of Fisher information matrix. Sweet.
     *  Hensman et al. Gaussian Processes for Big Data, 2013
     */
    val (naturalGradEta1, naturalaGradEta2) = calcLBGrad(j, beta, w, S, kZZ, kXZ, y, mP, mQ)

    val newTheta1 = theta1 + l * naturalGradEta1
    val newTheta2 = theta2 + l * naturalaGradEta2

    val newS = -0.5 * inv(newTheta2)
    val newM = newS * newTheta1

    (newM, newS)

  }
}