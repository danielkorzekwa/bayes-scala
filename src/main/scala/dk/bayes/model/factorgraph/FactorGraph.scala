package dk.bayes.model.factorgraph

import dk.bayes.model.factor.Factor

/**
 * Represents joined probability distribution as a factor graph.
 * http://en.wikipedia.org/wiki/Factor_graph
 *
 * @author Daniel Korzekwa
 */
trait FactorGraph {

  /**
   * Adds factor to the factor graph.
   */
  def addFactor(factor: Factor)

  /**
   * Returns all nodes (factors and variables) in a graph.
   */
  def getNodes(): Seq[Node]

  /**
   * Returns all factor nodes.
   */
  def getFactorNodes(): Seq[FactorNode]

  /**
   * Returns variable node.
   */
  def getVariableNode(varId: Int): VarNode

}