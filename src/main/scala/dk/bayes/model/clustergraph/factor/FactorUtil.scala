package dk.bayes.model.clustergraph.factor

/**
 * Factor utilities.
 *
 * @author Daniel Korzekwa
 */
object FactorUtil {

  /**
   * Returns vector normalised to 1.
   */
  def normalise(vector: Array[Double]): Array[Double] = {
    var normalisation = 0d
    val normalisedVector = new Array[Double](vector.size)

    var i = 0
    while (i < vector.size) {
      normalisation += vector(i)
      i += 1
    }

    i = 0
    while (i < vector.size) {
      normalisedVector(i) += vector(i) / normalisation
      i += 1
    }

    normalisedVector
  }

  /**
   * Returns value index for factor assignment.
   */
  def assignmentToIndex(assignment: Array[Int], stepSizes: Array[Int]): Int = {

    var assignmentIndex = 0
    var i = 0
    while (i < assignment.size) {
      assignmentIndex += assignment(i) * stepSizes(i)
      i += 1
    }
    assignmentIndex
  }

  /**
   * Returns step sizes for factor variables.
   * Step size - Number of steps before reaching next assignment for a given dimension.
   */
  def calcStepSizes(variables: Array[Var]): Array[Int] = {

    val varNum = variables.size

    val stepSizes = new Array[Int](varNum)

    if (varNum == 1) stepSizes(0) = 1
    else {

      var i = varNum - 1
      var product = 1
      while (i >= 0) {
        stepSizes(i) = product
        product *= variables(i).dim
        i -= 1
      }

    }

    stepSizes
  }

}