package dk.bayes.factor

import scala.Math._

/**
 * Represents factor for a multiple variables.
 *
 * @author Daniel Korzekwa
 *
 * @param variables Factor variables
 * @param values Factor values
 */
case class MultiFactor(variables: Array[Var], values: Array[Double]) extends Factor {

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

    val productValues = new Array[Double](values.size)
    i = 0
    var singleFactorIndex = 0
    while (i < values.size) {

      if (i > 0 && (i % singleFactorStepSize) == 0) {
        if (singleFactorIndex < singleFactor.getVariable().dim - 1) singleFactorIndex += 1
        else singleFactorIndex = 0
      }

      productValues(i) = values(i) * singleFactor.getValues()(singleFactorIndex)
      i += 1
    }

    MultiFactor(variables, productValues)
  }

  def withEvidence(evidence: Tuple2[Int, Int]): Factor = {

    val varIndex = findVariableIndex(evidence._1)

    val evidenceFactor = if (varIndex != -1) {

      val evidenceVariable = variables(varIndex)
      require(evidence._2 < evidenceVariable.dim, "Evidence value index is out of range")

      val stepSize = stepSizes(varIndex)
      val evidenceValues = new Array[Double](values.size)
      var evidenceIndex = 0

      var i = 0
      while (i < values.size) {

        if (i > 0 && (i % stepSize) == 0) {
          if (evidenceIndex < evidenceVariable.dim - 1) evidenceIndex += 1 else evidenceIndex = 0
        }

        if (evidenceIndex == evidence._2) evidenceValues(i) = values(i)

        i += 1
      }

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

      var i = 0
      var marginalIndex = 0
      while (i < values.size) {

        if (i > 0 && (i % stepSize) == 0) {
          if (marginalIndex < marginalVariable.dim - 1) marginalIndex += 1 else marginalIndex = 0
        }

        marginalValues(marginalIndex) = marginalValues(marginalIndex) + values(i)

        i += 1
      }

      SingleFactor(marginalVariable, marginalValues)

    } else throw new IllegalArgumentException("Variable not found:" + varId)

    marginalFactor
  }

  private def findVariableIndex(varId: Int): Int = {

    var i = 0
    while (i < variables.size) {
      val variable = variables(i)
      if (variable.id == varId) return i
      i += 1
    }
    -1
  }

  def normalise(): Factor = {
    val normalisedValues = FactorUtil.normalise(values)
    new MultiFactor(variables, normalisedValues)
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
    MultiFactor(Array(variable1, variable2), values)
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
    MultiFactor(Array(variable1, variable2, variable3), values)
  }
}