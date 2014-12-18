package dk.bayes.dsl

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.HashSet

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

  /**
   * Returns all variables in the Bayesian Network
   */
  def getAllVariables(): Seq[Variable] = {
    val variables = new HashSet[Variable]()

    def addVariable(v: Variable) {

      if (!variables.contains(v)) {
        variables += v

        v.getParents.foreach(p => addVariable(p))
        v.getChildren.foreach(c => addVariable(c))
      }

    }

    addVariable(this)

    variables.toList
  }

}