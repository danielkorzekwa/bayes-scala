package dk.bayes.factor

import Factor._

/**
 * Represents factor for a single variable.
 *
 * @author Daniel Korzekwa
 */
case class SingleFactor(variable: Var, values: Array[Double]) extends Factor {

  require(variable.dim == values.size, "Number of potential values must equal to variable dimension")

  def getVariable(): Var = variable

  def getVariables(): Array[Var] = Array(variable)

  def getValues(): Array[Double] = values

  def getValue(assignment: Array[Int]): Double = {

    require(assignment.size == 1, "Number of assignments must be 1")

    val singleAssignment = assignment(0)
    values(singleAssignment)
  }

  def product(singleFactor: SingleFactor): SingleFactor = {
    require(variable.id == singleFactor.variable.id, "Variable ids for product factors are not the same")
    require(variable.dim == singleFactor.variable.dim, "Variable dimensions for product factors are not the same")

    val productValues = new Array[Double](values.size)

    var i = 0
    while (i < values.size) {
      productValues(i) = values(i) * singleFactor.getValues()(i)
      i += 1
    }

    this.copy(values = productValues)
  }

  def withEvidence(evidence: Tuple2[Int, Int]): Factor = {
    require(variable.id == evidence._1, "Variable not found:" + evidence._1)

    val evidenceValues = new Array[Double](values.size)
    evidenceValues(evidence._2) = values(evidence._2)

    this.copy(values = evidenceValues)
  }

  def marginal(varId: Int): SingleFactor = {
    require(variable.id == varId, "Variable not found:" + varId)

    val marginalValues = new Array[Double](values.size)

    var i = 0
    while (i < values.size) {
      marginalValues(i) += values(i)
      i += 1
    }

    this.copy(values = marginalValues)
  }

  def normalise(): SingleFactor = {

    var normalisation = 0d
    val normalisedValues = new Array[Double](values.size)

    var i = 0
    while (i < values.size) {
      normalisation += values(i)
      i += 1
    }

    i = 0
    while (i < values.size) {
      normalisedValues(i) += values(i) / normalisation
      i += 1
    }

    this.copy(values = normalisedValues)
  }

}