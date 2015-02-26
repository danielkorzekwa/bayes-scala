package dk.bayes.model.factorgraph

import scala.collection._
import dk.bayes.model.factor.api.Factor
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer
import dk.bayes.model.factor.api.TripleFactor
import dk.bayes.model.factor.api.DoubleFactor
import dk.bayes.model.factor.api.SingleFactor
import dk.bayes.model.factor.api.GenericFactor

/**
 * Default implementation of a FactorGraph.
 *
 * @author Daniel Korzekwa
 */
case class GenericFactorGraph() extends FactorGraph {

  private val allNodes = ArrayBuffer[Node]()
  private val varNodes: mutable.Map[Int, VarNode] = mutable.Map[Int, VarNode]()
  private val factorNodes: mutable.Map[Seq[Int], FactorNode] = mutable.Map[Seq[Int], FactorNode]()

  def addFactor(factor: Factor) = {

    val missingVars = ArrayBuffer[VarNode]()

    //Get factor variable nodes and build the list of missing variable nodes
    val factorVarNodes = factor.getVariableIds().map(varId => varNodes.getOrElseUpdate(varId, {
      val varNode = VarNode(varId)
      missingVars += varNode
      varNode
    }))

    //Connect factor with variables using gates
    val factorGates = factorVarNodes.map { varNode =>

      val initialMsg = factor.marginal(varNode.varId)

      val factorGate = FactorGate(initialMsg)
      val varGate = VarGate(initialMsg, varNode)

      factorGate.setEndGate(varGate)
      varGate.setEndGate(factorGate)

      varNode.addGate(varGate)

      factorGate
    }.toVector

    val factorNode = factor match {
      case factor: SingleFactor if factorGates.size == 1 => new SingleFactorNode(factor, factorGates(0))
      case factor: DoubleFactor if factorGates.size == 2 => new DoubleFactorNode(factor, factorGates(0), factorGates(1))
      case factor: TripleFactor if factorGates.size == 3 => new TripleFactorNode(factor, factorGates(0), factorGates(1), factorGates(2))
      case factor: GenericFactor => new GenericFactorNode(factor, factorGates)
    }
    factorGates.foreach(g => g.setFactorNode(factorNode))

    allNodes += factorNode
    factorNodes += factorNode.getFactor.getVariableIds() -> factorNode

    missingVars.foreach(v => allNodes += v)

  }

  def getNodes(): IndexedSeq[Node] = allNodes

  def getFactorNodes(): Seq[FactorNode] = factorNodes.values.toList

  def getFactorNode(varIds: Seq[Int]): FactorNode = factorNodes(varIds)

  def getVariableNode(varId: Int): VarNode = varNodes(varId)

  def getVariables(): Seq[Int] = varNodes.keys.toList

  def merge(that: FactorGraph): FactorGraph = {
    require(getVariables().intersect(that.getVariables()).size == 0, "Can't merge factor graphs with shared variables")

    val mergedFactorGraph = GenericFactorGraph()

    this.getFactorNodes().foreach(n => mergedFactorGraph.addFactor(n.getFactor()))
    that.getFactorNodes().foreach(n => mergedFactorGraph.addFactor(n.getFactor()))

    mergedFactorGraph
  }

}

object GenericFactorGraph {

  /**
   * Add the new factor to a corresponding factor graph. If none of factor variables belong to existing factor graphs, then a new factor graph is created.
   * If the new factor belongs to more than one factor graphs, then those factor graphs are merged.
   *
   * @param factor Factor to be added to factor graph
   * @param factorGraphs List of candidate factor graphs that the new factor is to be added to
   *
   *  @param List of factor graphs after adding the new factor
   */
  def addFactor(factor: Factor, factorGraphs: Seq[FactorGraph]): Seq[FactorGraph] = {

    val (matchedFactorGraphs, otherFactorGraphs) = factorGraphs.partition { g =>
      !factor.getVariableIds.intersect(g.getVariables).isEmpty
    }

    val mergedFactorGraph = matchedFactorGraphs match {
      case Nil => GenericFactorGraph()
      case _ => matchedFactorGraphs.reduceLeft((a, b) => a.merge(b))
    }

    mergedFactorGraph.addFactor(factor)
    mergedFactorGraph :: otherFactorGraphs.toList
  }

}