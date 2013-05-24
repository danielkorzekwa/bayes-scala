package dk.bayes.learn.lds

import scala.math._

/**
 * Learning parameters in Linear Dynamic Systems.
 *
 * Based on:
 * Zoubin Ghahramani, Geoffrey E. Hinton. Parameter Estimation for Linear Dynamical Systems, 1996
 * http://www.gatsby.ucl.ac.uk/~zoubin/papers/tr-96-2.pdf
 */
trait LDSLearn {

  /**
   * M-step
   */

  /**
   * @param latentVariables The temporal sequence  of sequences of latent variables from time 1 (prior state) till time N
   */
  def newA(latentVariables: Seq[Seq[LatentVariable]]): Double

  /**
   * @param latentVariables The temporal sequence  of sequences of latent variables from time 1 (prior state) till time N
   */
  def newQ(latentVariables: Seq[Seq[LatentVariable]]): Double

}