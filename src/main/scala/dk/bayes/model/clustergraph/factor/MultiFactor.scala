package dk.bayes.model.clustergraph.factor

import scala.math._
import MultiFactor._

/**
 * Represents factor for multiple variables.
 *
 * @author Daniel Korzekwa
 *
 * @param variables Factor variables
 * @param values Factor values
 */
class MultiFactor(variables: Array[Var], values: Array[Double]) extends Factor {

  private val stepSizes: Array[Int] = FactorUtil.calcStepSizes(variables)
  private val dimProduct = stepSizes(0) * variables(0).dim
  require(dimProduct == values.size, "Number of potential values must equal to a product of variable dimensions")

  def getVariables(): Array[Var] = variables
  def getValues(): Array[Double] = values

  def getValue(assignment: Array[Int]): Double = {
    require(assignment.size == variables.size, "Number of assignments must equal to number of factor variables")

    var i = 0
    while (i < variables.size) {
      require(assignment(i) < variables(i).dim, "Assignment value is out of range:" + assignment(i))
      i += 1
    }

    val assignmentIndex = FactorUtil.assignmentToIndex(assignment, stepSizes)
    values(assignmentIndex)
  }

  def product(singleFactor: SingleFactor): MultiFactor = {

    var singleFactorStepSize = 0
    var i = 0
    var continue = true
    while (continue && i < variables.size) {
      if (variables(i).id == singleFactor.getVariable().id) {
        require(variables(i).dim == singleFactor.getVariable().dim, "Variable dimensions for product factors are not the same")
        singleFactorStepSize = stepSizes(i)
        continue = false
      }
      i += 1
    }
    require(singleFactorStepSize > 0, "Factor variable not found:" + singleFactor.getVariable().id)

    val singleFactorValues = singleFactor.getValues()
    val productValues = new Array[Double](values.size)

    processValues(singleFactor.getVariable().dim, singleFactorStepSize,
      (i: Int, varIndex: Int) => productValues(i) = values(i) * singleFactorValues(varIndex))

    MultiFactor(variables, productValues)
  }

  def withEvidence(evidence: Tuple2[Int, Int]): Factor = {

    val varIndex = findVariableIndex(evidence._1)

    val evidenceFactor = if (varIndex != -1) {

      val evidenceVariable = variables(varIndex)
      require(evidence._2 < evidenceVariable.dim, "Evidence value index is out of range")

      val stepSize = stepSizes(varIndex)
      val evidenceValues = new Array[Double](values.size)

      processValues(evidenceVariable.dim, stepSize,
        (i: Int, varIndex: Int) => if (varIndex == evidence._2) evidenceValues(i) = values(i))

      MultiFactor(variables, evidenceValues)

    } else throw new IllegalArgumentException("Variable not found:" + evidence._1)

    evidenceFactor
  }

  def marginal(varId: Int): SingleFactor = {

    val varIndex = findVariableIndex(varId)

    val marginalFactor = if (varIndex != -1) {

      val marginalVariable = variables(varIndex)
      val stepSize = stepSizes(varIndex)

      val marginalValues = new Array[Double](marginalVariable.dim)

      processValues(marginalVariable.dim, stepSize,
        (i: Int, varIndex: Int) => marginalValues(varIndex) += values(i))

      SingleFactor(marginalVariable, marginalValues)

    } else throw new IllegalArgumentException("Variable not found:" + varId)

    marginalFactor
  }

  def normalise(): Factor = {
    val normalisedValues = FactorUtil.normalise(values)
    new MultiFactor(variables, normalisedValues)
  }

  def copy(values: Array[Double]): MultiFactor = new MultiFactor(variables, values)

  def mapAssignments[T: Manifest](f: Array[Int] => T): Array[T] = {

    val valuesNum = values.size
    val mapping = new Array[T](valuesNum)

    var assignment = new Array[Int](variables.size)
    var i = 0
    while (i < valuesNum) {
      mapping(i) = f(assignment.clone)

      var dimIndex = variables.size - 1
      var continue = true
      while (continue && dimIndex >= 0) {

        if (assignment(dimIndex) < variables(dimIndex).dim - 1) {
          assignment(dimIndex) += 1
          continue = false
        } else assignment(dimIndex) = 0

        dimIndex -= 1
      }
      i += 1
    }

    mapping
  }

  /**
   * Iterates over all factor values, providing at every iteration value index and variable index.
   *
   * @param dim Variable dimension
   * @param stepSize Number of steps before reaching next assignment for a given dimension
   * @param process (valueIndex, variableIndex) => Unit
   */
  private def processValues(dim: Int, stepSize: Int, process: (Int, Int) => Unit) {

    var i = 0
    var varIndex = 0
    while (i < values.size) {

      process(i, varIndex)

      if (((i + 1) % stepSize) == 0) {
        if (varIndex < dim - 1) varIndex += 1 else varIndex = 0
      }

      i += 1
    }
  }

  /**
   * Returns variable index for variable id or -1 if variable not found.
   */
  private def findVariableIndex(varId: Int): Int = {

    var i = 0
    while (i < variables.size) {
      if (variables(i).id == varId) return i
      i += 1
    }
    -1
  }

}

object MultiFactor {

  /**
   * Creates multi factor.
   *
   * @param variable1 Factor variable
   * @param variable2 Factor variable
   * @param values Factor values
   */
  def apply(variable1: Var, variable2: Var, values: Array[Double]): MultiFactor = {
    new MultiFactor(Array(variable1, variable2), values)
  }

  /**
   * Creates multi factor.
   *
   * @param variable1 Factor variable
   * @param variable2 Factor variable
   * @param variable3 Factor variable
   * @param values Factor values
   */
  def apply(variable1: Var, variable2: Var, variable3: Var, values: Array[Double]): MultiFactor = {
    new MultiFactor(Array(variable1, variable2, variable3), values)
  }

  /**
   * Creates multi factor.
   *
   * @param variables Factor variables
   * @param values Factor values
   */
  def apply(variables: Array[Var], values: Array[Double]): MultiFactor = {
    new MultiFactor(variables, values)
  }
}