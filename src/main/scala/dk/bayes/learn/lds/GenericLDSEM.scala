package dk.bayes.learn.lds

import com.typesafe.scalalogging.slf4j.LazyLogging
import dk.bayes.math.gaussian.canonical.CanonicalGaussian
import dk.bayes.model.factor.LinearGaussianFactor
import dk.bayes.model.factor.GaussianFactor
import dk.bayes.model.factorgraph.GenericFactorGraph
import dk.bayes.infer.ep.GenericEP
import dk.bayes.infer.ep.calibrate.fb.ForwardBackwardEPCalibrate
import java.util.concurrent.atomic.AtomicInteger
import scala.annotation.tailrec
import dk.bayes.math.gaussian.Gaussian
import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian

object GenericLDSEM extends LDSEM with LazyLogging {

  def learn(data: Array[Array[Double]], priorMean: Gaussian, emissionVar: Double, iterNum: Int): EMSummary = {

    @tailrec
    def emIteration(currPriorMean: Gaussian, currEmissionVar: Double, currIter: Int): EMSummary = {

      val stats: Seq[Stats] = data.map(d => eStep(currPriorMean, currEmissionVar, d))
      val (newPriorMean, newEmissionVar) = mStep(stats)

      logger.info(s"New lds parameters (iter=${currIter}: ${newPriorMean}, ${newEmissionVar}")
      if (currIter < iterNum) emIteration(newPriorMean, newEmissionVar, currIter + 1)
      else EMSummary(newPriorMean, newEmissionVar, currIter)
    }

    val emSummary = emIteration(priorMean, emissionVar, 1)
    emSummary
  }

  private def eStep(priorMean: Gaussian, emissionVar: Double, data: Array[Double]): Stats = {
    val nextVarId = new AtomicInteger(1)
    val factorGraph = GenericFactorGraph()

    val priorMeanFactor = GaussianFactor(nextVarId.getAndIncrement(), priorMean.m, priorMean.v)
    factorGraph.addFactor(priorMeanFactor)

    val pointFactors = data.map { d =>
      val pointFactor = LinearGaussianFactor(priorMeanFactor.varId, nextVarId.getAndIncrement(), a = 1, b = 0, v = emissionVar, evidence = Some(d))
      pointFactor
    }
    pointFactors.foreach(f => factorGraph.addFactor(f))

    val epSummary = ForwardBackwardEPCalibrate(factorGraph).calibrate(100, progress => {})
    require(epSummary.iterNum < 100, "LDS E-step takes max (100) number of interations to converge")
    logger.debug(s"E step summary: ${epSummary}")

    val genericEP = GenericEP(factorGraph)

    val priorMeanMarginal = genericEP.marginal(priorMeanFactor.varId).asInstanceOf[GaussianFactor]

    Stats(DenseCanonicalGaussian(priorMeanMarginal.m, priorMeanMarginal.v), data)
  }

  /**
   *  Returns (priorMean,emission variance)
   */
  private def mStep(stats: Seq[Stats]): Tuple2[Gaussian, Double] = {

    val priorMeanStats = stats.map(s => s.priorMean).toIndexedSeq
    val newPriorMean = GenericLDSLearn.newPi(priorMeanStats)
    val newPriorVariance = GenericLDSLearn.newV(priorMeanStats)

    val emissionStats: IndexedSeq[Tuple2[DenseCanonicalGaussian, Double]] = stats.flatMap(stat => stat.data.map(d => (stat.priorMean, d))).toIndexedSeq
    val newEmissionVariance = GenericLDSLearn.newR(emissionStats)

    (Gaussian(newPriorMean, newPriorVariance), newEmissionVariance)
  }

  private case class Stats(priorMean: DenseCanonicalGaussian, data: Array[Double])

}

