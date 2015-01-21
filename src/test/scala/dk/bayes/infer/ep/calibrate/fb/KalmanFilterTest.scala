package dk.bayes.infer.ep.calibrate.fb

import org.junit._
import Assert._
import dk.bayes.model.factor.MvnGaussianFactor
import dk.bayes.math.gaussian.canonical.CanonicalGaussian
import dk.bayes.math.linear.Matrix
import dk.bayes.model.factor.LinearGaussianFactor
import dk.bayes.model.factorgraph.GenericFactorGraph
import dk.bayes.infer.ep.GenericEP
import dk.bayes.model.factor.MvnLinearGaussianFactor
import breeze.stats.distributions.MultivariateGaussian
import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian

class KalmanFilterTest {

  @Test def test {

    val priorMvn = MvnGaussianFactor(varId = 1, DenseCanonicalGaussian(m = Matrix(0.92, 0.98), v = Matrix(2, 2, Array(1d, 0.5, 0.5, 1))))

    val prior = MvnLinearGaussianFactor(parentVarId = priorMvn.varId, varId = 2, a = Matrix(1, 0).t, b = 0, v = 1e-10)
    val likelihood1 = LinearGaussianFactor(prior.varId, 3, a = 1, b = 0, v = 2, evidence = Some(0.9))
    val likelihood2 = LinearGaussianFactor(prior.varId, 4, a = 1, b = 0, v = 2, evidence = Some(0.87))

    val factorGraph = GenericFactorGraph()

    factorGraph.addFactor(priorMvn)
    factorGraph.addFactor(prior)
    factorGraph.addFactor(likelihood1)
    factorGraph.addFactor(likelihood2)

    ForwardBackwardEPCalibrate(factorGraph).calibrate(10, (iterNum) => Unit)
    val ep = GenericEP(factorGraph)

    val posteriorMean = ep.marginal(priorMvn.varId).asInstanceOf[MvnGaussianFactor].canonGaussian.mean
    val posteriorVar = ep.marginal(priorMvn.varId).asInstanceOf[MvnGaussianFactor].canonGaussian.variance
    assertEquals(0.902, posteriorMean(0), 0.001)
    assertEquals(0.971, posteriorMean(1), 0.001)

    assertEquals(0.500, posteriorVar(0, 0), 0.001)
    assertEquals(0.250, posteriorVar(0, 1), 0.001)
    assertEquals(0.250, posteriorVar(1, 0), 0.001)
    assertEquals(0.875, posteriorVar(1, 1), 0.001)

  }

}