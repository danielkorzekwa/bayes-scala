package dk.bayes.model.factorgraph

import scala.collection._
import scala.collection.mutable.ListBuffer
import dk.bayes.model.factor.Factor

/**
 * Default implementation of a FactorGraph.
 *
 * @author Daniel Korzekwa
 */
case class GenericFactorGraph extends FactorGraph {

  private val allNodes = ListBuffer[Node]()
  private val varNodes: mutable.Map[Int, VarNode] = mutable.Map[Int, VarNode]()
  private val factorNodes: mutable.Map[Seq[Int], FactorNode] = mutable.Map[Seq[Int], FactorNode]()

  def addFactor(factor: Factor) = {

    val factorNode = new FactorNode(factor)
    allNodes += factorNode
    factorNodes += factorNode.getFactor.getVariableIds() -> factorNode

    //Add missing variable nodes
    val factorVarNodes = factor.getVariableIds().map(varId => varNodes.getOrElseUpdate(varId, {
      val varNode = VarNode(varId)
      allNodes += varNode
      varNode
    }))

    //Connect factor with variables using gates
    factorVarNodes.foreach { varNode =>

      val initialMsg = factor.marginal(varNode.varId)

      val factorGate = FactorGate(factorNode)
      factorGate.setMessage(initialMsg, -1)
      val varGate = VarGate(varNode)
      varGate.setMessage(initialMsg, -1)

      factorGate.setEndGate(varGate)
      varGate.setEndGate(factorGate)

      factorNode.addGate(factorGate)
      varNode.addGate(varGate)
    }

  }

  def getNodes(): Seq[Node] = allNodes

  def getFactorNodes(): Seq[FactorNode] = factorNodes.values.toList

  def getFactorNode(varIds: Seq[Int]): FactorNode = factorNodes(varIds)

  def getVariableNode(varId: Int): VarNode = varNodes(varId)

  def getVariables(): Seq[Int] = varNodes.keys.toList

  def merge(that: FactorGraph): FactorGraph = {
    require(getVariables().intersect(that.getVariables()).size == 0, "Can't merge factor graphs with shared variables")

    val mergedFactorGraph = GenericFactorGraph()

    this.getFactorNodes().foreach(n => mergedFactorGraph.addFactor(n.factor))
    that.getFactorNodes().foreach(n => mergedFactorGraph.addFactor(n.factor))

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