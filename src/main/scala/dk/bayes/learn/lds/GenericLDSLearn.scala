package dk.bayes.learn.lds

import scala.math._

/**
 * Default implementation of GenericLDSLearn.
 *
 * @author Daniel Korzekwa
 */
object GenericLDSLearn extends LDSLearn {

  /**
   * M-step
   */

  def newA(latentVariables: Seq[Seq[LatentVariable]]): Double = {

    /**Sequence sequences of expectations. Tuple[E[XU],E[UU]], where X - current time slice, U - previous time slice */
    val expectations: Seq[Seq[Tuple2[Double, Double]]] = for (latentVariablesSeq <- latentVariables) yield latentVariablesSeq.sliding(2).map {
      case Seq(u, x) => {
        val eXU = x.covariance.get + x.mean * u.mean
        val eUU = u.variance + pow(u.mean, 2)

        (eXU, eUU)
      }
      case _ => throw new IllegalArgumentException("At least two time slices are required")

    }.toSeq

    val singleSequence = expectations.flatten
    val A = singleSequence.map(_._1).sum / singleSequence.map(_._2).sum
    A
  }

  def newQ(latentVariables: Seq[Seq[LatentVariable]]): Double = {

    val A = newA(latentVariables)

    val QValues: Seq[Seq[Double]] = for (latentVariablesSeq <- latentVariables) yield latentVariablesSeq.sliding(2).map {
      case Seq(u, x) =>
        val eXU = x.covariance.get + x.mean * u.mean
        val eXX = x.variance + pow(x.mean, 2)

        eXX - A * eXU
    }.toSeq

    val singleSequenceQValues = QValues.flatten
    val Q = singleSequenceQValues.sum / singleSequenceQValues.size
    Q
  }

}