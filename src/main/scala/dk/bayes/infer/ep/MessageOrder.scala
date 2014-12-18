package dk.bayes.infer.ep

import dk.bayes.model.factorgraph.Node

/**
 * Defines the order of messages that are sent between nodes in a factor graph during calibration process.
 *
 *  @author Daniel Korzekwa
 */
trait MessageOrder {

  /**
   * Returns the order, in which messages are sent between nodes in a factor graph during calibration process.
   *
   * @nodes All nodes (variables and factors in a factor graph)
   *
   */
  def ordered(nodes: Seq[Node]): Seq[Node]
}