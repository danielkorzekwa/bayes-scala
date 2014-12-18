package dk.bayes.model.factorgraph

import dk.bayes.model.factor.api.Factor

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
  def getNodes(): IndexedSeq[Node]

  /**
   * Returns all factor nodes in a factor graph.
   */
  def getFactorNodes(): Seq[FactorNode]

  /**
   * Returns factor node.
   */
  def getFactorNode(varIds: Seq[Int]): FactorNode

  /**
   * Returns variable node.
   */
  def getVariableNode(varId: Int): VarNode

  /**
   *  Returns the list of variables in a factor graph.
   */
  def getVariables(): Seq[Int]

  /**
   * Merges this and that factor graphs, putting all factors,
   * variables and factor gates from both factor graphs into the new factor graph.
   */
  def merge(that: FactorGraph): FactorGraph

}