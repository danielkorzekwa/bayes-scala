package dk.bayes.dsl

import scala.collection.mutable.ListBuffer

/**
 * Variable in a Bayesian Network
 *
 *  @author Daniel Korzekwa
 */
trait Variable {

  /**
   * Definitions
   */
  def getParents(): Seq[Variable]

  /**
   * Implementation
   */
  var children = ListBuffer[Variable]()

  init()

  private def init() {
    getParents().foreach(p => p.addChild(this))
  }

  def addChild(v: Variable) {
    children += v
  }
  def getChildren(): Seq[Variable] = children.toList

  def hasParents(): Boolean = !getParents.isEmpty
  def hasChildren(): Boolean = !getChildren.isEmpty

}