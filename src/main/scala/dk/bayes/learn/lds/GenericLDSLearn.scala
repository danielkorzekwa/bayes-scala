package dk.bayes.learn.lds

import scala.math._
import dk.bayes.model.factor.BivariateGaussianFactor

/**
 * Default implementation of GenericLDSLearn.
 *
 * @author Daniel Korzekwa
 */
object GenericLDSLearn extends LDSLearn {

  /**
   * M-step
   */
  def newA(sStats: IndexedSeq[TransitionStat]): Double = {
    require(!sStats.isEmpty, "Sufficient statistics are empty")

    /**Sequences of expectations. Tuple2[E[XU],E[UU]], where X - current time slice, U - previous time slice */
    val expectations: Seq[Tuple2[Double, Double]] = sStats.map { s =>

      val eXU = s.cov + s.t1Mean * s.t0Mean
      val eUU = s.t0Var + pow(s.t0Mean, 2)

      (eXU, eUU)

    }

    val (eXUSum, eUuSum) = sum(expectations)
    eXUSum / eUuSum
  }

  def newQ(sStats: IndexedSeq[TransitionStat]): Double = {
    require(!sStats.isEmpty, "Sufficient statistics are empty")

    val A = newA(sStats)

    val QValues: IndexedSeq[Double] = sStats.map { s =>
      val eXU = s.cov + s.t1Mean * s.t0Mean
      val eXX = s.t1Var + pow(s.t1Mean, 2)

      eXX - A * eXU
    }

    val Q = QValues.sum / QValues.size
    Q
  }

  def newPi(sStats: IndexedSeq[PriorStat]): Double = {
    require(!sStats.isEmpty, "Sufficient statistics are empty")
    sStats.map(s => s.m).sum / sStats.size
  }

  /**
   *  = E[x^2] - E[x]^2
   */
  def newV(sStats: IndexedSeq[PriorStat]): Double = {
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