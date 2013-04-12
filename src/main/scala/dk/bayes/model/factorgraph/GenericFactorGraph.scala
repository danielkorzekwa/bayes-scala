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

  def addFactor(factor: Factor) = {

    val factorNode = FactorNode(factor)
    allNodes += factorNode

    //Add missing variable nodes
    val factorVarNodes = factor.getVariableIds().map(varId => varNodes.getOrElseUpdate(varId, {
      val varNode = VarNode(varId)
      allNodes += varNode
      varNode
    }))

    //Connect factor with variables using gates
    factorVarNodes.foreach { varNode =>

      val initialMsg = factor.marginal(varNode.varId)
      
      val factorGate = Gate()
      factorGate.setMessage(initialMsg)
      val varGate = Gate()
      varGate.setMessage(initialMsg)

      factorGate.setEndGate(varGate)
      varGate.setEndGate(factorGate)

      factorNode.addGate(factorGate)
      varNode.addGate(varGate)
    }

  }

  def getNodes(): Seq[Node] = allNodes.toList

  def getVariableNode(varId: Int): VarNode = varNodes(varId)

}