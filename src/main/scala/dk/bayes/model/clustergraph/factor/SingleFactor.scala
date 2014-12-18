package dk.bayes.model.clustergraph.factor

/**
 * Represents factor for a single variable.
 *
 * @author Daniel Korzekwa
 *
 * @param variable Factor variable
 * @param values Factor values
 */
class SingleFactor(variable: Var, values: Array[Double]) extends Factor {

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
    require(variable.id == singleFactor.getVariable().id, "Variable ids for product factors are not the same")
    require(variable.dim == singleFactor.getVariable().dim, "Variable dimensions for product factors are not the same")

    val productValues = new Array[Double](values.size)

    var i = 0
    while (i < values.size) {
      productValues(i) = values(i) * singleFactor.getValues()(i)
      i += 1
    }

    SingleFactor(variable, productValues)
  }

  def withEvidence(evidence: Tuple2[Int, Int]): SingleFactor = {
    require(variable.id == evidence._1, "Variable not found:" + evidence._1)

    val evidenceValues = new Array[Double](values.size)
    evidenceValues(evidence._2) = values(evidence._2)

    SingleFactor(variable, evidenceValues)
  }

  def marginal(varId: Int): SingleFactor = {
    require(variable.id == varId, "Variable not found:" + varId)

    val marginalValues = new Array[Double](values.size)

    var i = 0
    while (i < values.size) {
      marginalValues(i) += values(i)
      i += 1
    }

    SingleFactor(variable, marginalValues)
  }

  def normalise(): SingleFactor = {

    val normalisedValues = FactorUtil.normalise(values)
    SingleFactor(variable, normalisedValues)
  }

  def copy(values: Array[Double]): SingleFactor = new SingleFactor(variable, values)

  def mapAssignments[T: Manifest](f: Array[Int] => T): Array[T] = {

    val mapping: Array[T] = new Array[T](values.size)

    var i = 0
    while (i < values.size) {
      mapping(i) = f(Array(i))
      i += 1
    }

    mapping
  }

}

object SingleFactor {

  /**
   * Creates single factor.
   *
   * @param variable Factor variable
   * @param values Factor values
   */
  def apply(variable: Var, values: Array[Double]): SingleFactor = new SingleFactor(variable, values)
}