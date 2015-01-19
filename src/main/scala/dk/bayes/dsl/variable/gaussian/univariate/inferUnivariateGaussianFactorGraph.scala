package dk.bayes.dsl.variable.gaussian.univariate

import dk.bayes.dsl.InferEngine
import java.util.concurrent.atomic.AtomicInteger
import dk.bayes.dsl.Variable
import dk.bayes.model.factor.GaussianFactor
import dk.bayes.model.factor.LinearGaussianFactor
import dk.bayes.model.factor.api.Factor
import dk.bayes.dsl.variable.categorical.CdfThresholdCategorical
import dk.bayes.model.factor.TruncGaussianFactor
import dk.bayes.math.linear.Matrix
import dk.bayes.model.factor.DiffGaussianFactor
import dk.bayes.model.factorgraph.GenericFactorGraph
import dk.bayes.infer.ep.GenericEP
import dk.bayes.infer.ep.calibrate.fb.ForwardBackwardEPCalibrate
import dk.bayes.dsl.variable.gaussian.univariatelinear.UnivariateLinearGaussian

object inferUnivariateGaussianFactorGraph extends InferEngine[UnivariateGaussian, UnivariateGaussian] {

  def isSupported(x: UnivariateGaussian): Boolean = true

  def infer(x: UnivariateGaussian): UnivariateGaussian = {

    //Create factor graph variables
    val nextVarId = new AtomicInteger(1)
    val factorGraphVarsMap: Map[Variable, Int] = x.getAllVariables.map(v => v -> nextVarId.getAndIncrement()).toMap

    //Create factors
    val factors: Seq[Factor] = factorGraphVarsMap.keys.map(v => toFactor(v, factorGraphVarsMap)).toList

    //Build factor graph and run the inference
    val factorGraph = GenericFactorGraph()
    factors.foreach(f => factorGraph.addFactor(f))

    val epCalibrate = ForwardBackwardEPCalibrate(factorGraph)
    epCalibrate.calibrate(10, (iter) => {})
    val ep = GenericEP(factorGraph)

    val varMarginal = ep.marginal(factorGraphVarsMap(x)).asInstanceOf[GaussianFactor]
    
    new UnivariateGaussian(varMarginal.m,varMarginal.v)
  }

  private def toFactor(v: Variable, factorGraphVarsMap: Map[Variable, Int]): Factor = {

    try {
    val factorVarId = factorGraphVarsMap(v)
    val factor: Factor = v match {
      case v: UnivariateGaussian => GaussianFactor(factorVarId, v.m, v.v)
      case v: UnivariateLinearGaussian if (v.x.size == 1 && v.yValue.isEmpty) => {
        val parentVarId = factorGraphVarsMap(v.x.head)
        val varId = factorVarId
        LinearGaussianFactor(parentVarId, varId, a = v.a(0), b = v.b, v = v.v)
      }
      //special case for a difference of two gaussians
      case v: UnivariateLinearGaussian if (v.x.size == 2 && v.a.isIdentical(Matrix(1, -1), 0) && v.v==0) => {
        val gaussian1VarId = factorGraphVarsMap(v.x(0))
        val gaussian2VarId = factorGraphVarsMap(v.x(1))
        val diffGaussianVarId = factorVarId
        DiffGaussianFactor(gaussian1VarId, gaussian2VarId, diffGaussianVarId)
      }
      case v: CdfThresholdCategorical if (v.value.isDefined) => {
        val gaussianVarId = factorGraphVarsMap(v.x)
        val truncVarId = factorVarId
        val evidence = if (v.value.get == 0) true else false
        TruncGaussianFactor(gaussianVarId, truncVarId, 0, Some(evidence))
      }

    }
    factor
    }
    catch {
      case e:MatchError => throw new UnsupportedOperationException("Inference not supported",e)
    }
  }

}