package dk.bayes.learn.lds

import scala.math._
import dk.bayes.model.factor.BivariateGaussianFactor
import dk.bayes.math.gaussian.canonical.CanonicalGaussian
import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian

/**
 * Default implementation of GenericLDSMStep. It supports one dimensional linear dynamical system only, where both state and observations are 1-D real vectors.
 *
 * @author Daniel Korzekwa
 */
object GenericLDSLearn extends LDSMStep {

  /**
   * M-step
   */

  def newC(sStats: IndexedSeq[Tuple2[DenseCanonicalGaussian, Double]]): Double = {

    /**Sequence of observations. Tuple2(y * E[xGivenY], E[XX]), where X - prior mean, Y - observed value*/
    val expectations: Seq[Tuple2[Double, Double]] = sStats.map {
      case (xGivenY, y) =>
        val yx = y * xGivenY.mean(0)
        val eXX = xGivenY.variance(0, 0) + pow(xGivenY.mean(0), 2)

        (yx, eXX)
    }

    val (yxSum, eXXSum) = sum(expectations)
    yxSum / eXXSum
  }

  def newR(sStats: IndexedSeq[Tuple2[DenseCanonicalGaussian, Double]]): Double = {

    val newC = this.newC(sStats)

    val rTerms = sStats.map { case (xGivenY, y) => y * y - newC * xGivenY.mean(0) * y }

    val R = rTerms.sum / rTerms.size
    R

  }

  def newA(sStats: IndexedSeq[DenseCanonicalGaussian]): Double = {
    require(!sStats.isEmpty, "Sufficient statistics are empty")

    /**Sequences of expectations. Tuple2[E[XU],E[UU]], where X - current time slice, U - previous time slice */
    val expectations: Seq[Tuple2[Double, Double]] = sStats.map { s =>

      val eXU = s.variance(0, 1) + s.mean(1) * s.mean(0)
      val eUU = s.variance(0, 0) + pow(s.mean(0), 2)

      (eXU, eUU)

    }

    val (eXUSum, eUuSum) = sum(expectations)
    eXUSum / eUuSum
  }

  def newQ(sStats: IndexedSeq[DenseCanonicalGaussian]): Double = {
    require(!sStats.isEmpty, "Sufficient statistics are empty")

    val A = newA(sStats)

    val QValues: IndexedSeq[Double] = sStats.map { s =>
      val eXU = s.variance(0, 1) + s.mean(1) * s.mean(0)
      val eXX = s.variance(1, 1) + pow(s.mean(1), 2)

      eXX - A * eXU
    }

    val Q = QValues.sum / QValues.size
    Q
  }

  def newPi(sStats: IndexedSeq[DenseCanonicalGaussian]): Double = {
    require(!sStats.isEmpty, "Sufficient statistics are empty")
    sStats.map(s => s.m).sum / sStats.size
  }

  /**
   *  = E[x^2] - E[x]^2
   */
  def newV(sStats: IndexedSeq[DenseCanonicalGaussian]): Double = {
    require(!sStats.isEmpty, "Sufficient statistics are empty")

    //computing E[xx]
    val Exx = sStats.map(s => s.v + pow(s.m, 2)).sum / sStats.size

    //computing E[x]
    val Ex = newPi(sStats)

    val V = Exx - pow(Ex, 2)
    V
  }

  private def sum(sequence: Seq[Tuple2[Double, Double]]): Tuple2[Double, Double] = {
    sequence.reduceLeft((total, expectation) => (total._1 + expectation._1, total._2 + expectation._2))
  }
}