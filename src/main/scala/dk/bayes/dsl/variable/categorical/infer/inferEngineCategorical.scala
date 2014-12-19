package dk.bayes.dsl.variable.categorical.infer

import dk.bayes.dsl.InferEngine
import dk.bayes.dsl.variable.Categorical
import java.util.concurrent.atomic.AtomicInteger
import dk.bayes.model.clustergraph.factor.Var
import dk.bayes.model.clustergraph.factor.Factor
import dk.bayes.model.clustergraph.ClusterGraph
import dk.bayes.infer.LoopyBP

object inferEngineCategorical extends InferEngine[Categorical, Categorical] {

  def isSupported(x: Categorical): Boolean = {
    val nonCategoricalVars = x.getAllVariables.find(v => !v.isInstanceOf[Categorical])
    nonCategoricalVars.isEmpty
  }

  def infer(x: Categorical): Categorical = {

    val variables = x.getAllVariables().map(v => v.asInstanceOf[Categorical])

    //Create cluster graph variables
    val nextVarId = new AtomicInteger(1)
    val clusterGraphVarsMap: Map[Categorical, Var] = variables.map(v => v -> Var(nextVarId.getAndIncrement(), v.dim)).toMap

    //Create factors
    val factors = clusterGraphVarsMap.map {
      case (v, clusterVar) =>
        val factorParentVars = v.getParents().map(c => clusterGraphVarsMap(c.asInstanceOf[Categorical]))
        val allVars = factorParentVars :+ clusterVar
        Factor(allVars.toArray, v.cpd.toArray)
    }

    //Create factor graph and add clusters 
    val clusterGraph = ClusterGraph()
    factors.foreach(f => clusterGraph.addCluster(f.getVariables.last.id, f))

    //Add edges between clusters
    clusterGraphVarsMap.foreach {
      case (v, clusterVar) =>
        v.getParents.foreach(p => clusterGraph.addEdge(clusterVar.id, clusterGraphVarsMap(p).id))
    }

    //Create inference engine
    val loopyBP = LoopyBP(clusterGraph)

    //Set evidence
    clusterGraphVarsMap.foreach {
      case (v, clusterVar) if (v.getValue().isDefined) => loopyBP.setEvidence(clusterVar.id, v.getValue().get)
      case _ => //do nothing
    }

    //Run the inference
    loopyBP.calibrate()
    val varId = clusterGraphVarsMap(x).id
    val marginal = loopyBP.marginal(varId)

    Categorical(marginal.getValues())
  }

}