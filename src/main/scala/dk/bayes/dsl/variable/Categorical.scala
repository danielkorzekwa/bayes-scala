package dk.bayes.dsl.variable

import dk.bayes.dsl.InferEngine
import dk.bayes.model.clustergraph.factor.Var
import dk.bayes.dsl.Variable
import dk.bayes.model.clustergraph.ClusterGraph
import dk.bayes.infer.LoopyBP
import dk.bayes.infer._
import java.util.concurrent.atomic.AtomicInteger
import dk.bayes.model.clustergraph.factor.Factor

/**
 * Categorical variable (http://en.wikipedia.org/wiki/Categorical_distribution)
 *
 * @author Daniel Korzekwa
 */

class Categorical(val parents: Seq[Categorical], val cpd: Seq[Double]) extends Variable {

  val dim: Int = cpd.size / parents.map(p => p.dim).product
  private var value: Option[Int] = None

  def setValue(v: Int) { value = Some(v) }

  def getParents(): Seq[Categorical] = parents
}

object Categorical {

  def apply(cpd: Seq[Double]) = new Categorical(Vector(), cpd)

  def apply(parent: Categorical, cpd: Seq[Double]): Categorical = new Categorical(Vector(parent), cpd)

  def apply(parent1: Categorical, parent2: Categorical, cpd: Seq[Double]): Categorical = new Categorical(Vector(parent1, parent2), cpd)

  implicit val inferEngine = new InferEngine[Categorical, Categorical] {

    def infer(x: Categorical): Categorical = {

      val variables = x.getAllVariables().map { v =>
        require(v.isInstanceOf[Categorical], "Inference not supported")
        v.asInstanceOf[Categorical]
      }

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
        case (v, clusterVar) if (v.value.isDefined) => loopyBP.setEvidence(clusterVar.id, v.value.get)
        case _ => //do nothing
      }

      //Run the inference
      loopyBP.calibrate()
      val varId = clusterGraphVarsMap(x).id
      val marginal = loopyBP.marginal(varId)

      Categorical(marginal.getValues())
    }
  }
}