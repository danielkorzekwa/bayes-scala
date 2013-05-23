package dk.bayes.model.factor

import scala.math._

/**
 * This class represents a multinomial probability distribution over a set of discrete variables.
 *
 * @author Daniel Korzekwa
 *
 * @param variableIds Factor variables
 * @param variableDims Variable dimensions
 * @param values Factor values
 */
case class TableFactor(variableIds: Seq[Int], variableDims: Seq[Int], valueProbs: Array[Double]) extends Factor {

  def getVariableIds(): Seq[Int] = variableIds

  def marginal(varId: Int): TableFactor = throw new UnsupportedOperationException("Not implemented yet")

  def productMarginal(varId: Int, factors: Seq[Factor]): Factor = throw new UnsupportedOperationException("Not implemented yet")

  def withEvidence(varId: Int, varValue: AnyVal): TableFactor = variableIds match {
    case Seq(varId) => withEvidenceForSingleFactor(varId, varValue.asInstanceOf[Int])
    case _ => throw new UnsupportedOperationException("Not implemented yet")
  }

  private def withEvidenceForSingleFactor(varId: Int, varValue: Int): TableFactor = {
    require(varId == variableIds.head, "Variable not found:" + varId)

    val evidenceValueProbs = new Array[Double](valueProbs.size)
    evidenceValueProbs(varValue) = valueProbs(varValue)

    this.copy(valueProbs = evidenceValueProbs)
  }

  def getValue(assignment: (Int, AnyVal)*): Double = {
    val value = variableIds match {
      case Seq(varId) => getValueForSingleFactor(assignment.asInstanceOf[Seq[Tuple2[Int, Int]]])
      case _ => throw new UnsupportedOperationException("Not implemented yet")
    }

    value
  }

  private def getValueForSingleFactor(assignment: Seq[Tuple2[Int, Int]]): Double = {
    val value = assignment match {
      case Seq((varId: Int, varValue: Int)) if varId == variableIds.head => valueProbs(varValue)
      case _ => throw new IllegalArgumentException("Factor assignment does not match single variable factor: " + assignment)
    }
    value
  }

  def *(factor: Factor): TableFactor = {
   
    variableIds match {
      case Seq(varId) => productForSingleFactor(factor.asInstanceOf[TableFactor])
      case _ => throw new UnsupportedOperationException("Not implemented yet")
    }
  }

  private def productForSingleFactor(factor: TableFactor): TableFactor = {
    require(variableIds.head == factor.variableIds.head, "Variable ids for product factors are not the same")
    require(variableDims.head == factor.variableDims.head, "Variable dimensions for product factors are not the same")

    val quotientValues = new Array[Double](valueProbs.size)

    var i = 0
    while (i < quotientValues.size) {
      quotientValues(i) = valueProbs(i) * factor.valueProbs(i)
      i += 1
    }

    TableFactor(variableIds, variableDims, quotientValues)
  }

  def /(that: Factor): TableFactor = variableIds match {
    case Seq(varId) => divideForSingleFactor(that.asInstanceOf[TableFactor])
    case _ => throw new UnsupportedOperationException("Not implemented yet")
  }

  private def divideForSingleFactor(factor: TableFactor): TableFactor = {
    require(variableIds.head == factor.variableIds.head, "Variable ids for quotient factors are not the same")
    require(variableDims.head == factor.variableDims.head, "Variable dimensions for quotient factors are not the same")

    val quotientValues = new Array[Double](valueProbs.size)

    val factorsAreEqual = this.equals(factor)
    var i = 0
    while (i < quotientValues.size) {
      if (factorsAreEqual) quotientValues(i) = 1
      else quotientValues(i) = valueProbs(i) / factor.valueProbs(i)
      i += 1
    }

    TableFactor(variableIds, variableDims, quotientValues)
  }

  def equals(that: Factor, threshold: Double): Boolean = {
    val tableFactor = that.asInstanceOf[TableFactor]
    val notTheSame = valueProbs.zip(tableFactor.valueProbs).find { case (thisVal, thatVal) => abs(thisVal - thatVal) > threshold }
    notTheSame.isEmpty
  }

  override def toString() = "TableFactor(%s,%s,%s)".format(variableIds, variableDims, valueProbs.toList)
}