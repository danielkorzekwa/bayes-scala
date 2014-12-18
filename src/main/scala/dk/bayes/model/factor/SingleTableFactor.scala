package dk.bayes.model.factor

import scala.math._
import dk.bayes.model.factor.api.Factor
import dk.bayes.model.factor.api.SingleFactor

/**
 * This class represents a multinomial probability distribution over a single discrete variable.
 *
 * @author Daniel Korzekwa
 *
 * @param variableId Factor variable id
 * @param variableDim Variable dimension
 * @param values Factor values
 */
case class SingleTableFactor(varId: Int, variableDim: Int, valueProbs: Array[Double]) extends SingleFactor {
  
  def getVariableId(): Int = varId

  def marginal(varId: Int): SingleTableFactor = throw new UnsupportedOperationException("Not implemented yet")

  def productMarginal(varId: Int, factors: Seq[Factor]): SingleTableFactor = throw new UnsupportedOperationException("Not implemented yet")

  override def withEvidence(varId: Int, varValue: AnyVal): SingleTableFactor = {

    def withEvidenceForSingleFactor(evidenceVarId: Int, varValue: Int): SingleTableFactor = {
      require(evidenceVarId == varId, "Variable not found:" + evidenceVarId)

      val evidenceValueProbs = new Array[Double](valueProbs.size)
      evidenceValueProbs(varValue) = valueProbs(varValue)

      this.copy(valueProbs = evidenceValueProbs)
    }

    withEvidenceForSingleFactor(varId, varValue.asInstanceOf[Int])

  }

  override def getValue(assignment: (Int, AnyVal)*): Double = {

    def getValueForSingleFactor(assignment: Seq[Tuple2[Int, Int]]): Double = {
      val value = assignment match {
        case Seq((evidenceVarId: Int, varValue: Int)) if evidenceVarId == varId => valueProbs(varValue)
        case _ => throw new IllegalArgumentException("Factor assignment does not match single variable factor: " + assignment)
      }
      value
    }

    getValueForSingleFactor(assignment.asInstanceOf[Seq[Tuple2[Int, Int]]])
  }

  override def *(factor: Factor): SingleTableFactor = {

    def productForSingleFactor(factor: SingleTableFactor): SingleTableFactor = {
      require(varId == factor.varId, "Variable ids for product factors are not the same")
      require(variableDim == factor.variableDim, "Variable dimensions for product factors are not the same")

      val quotientValues = new Array[Double](valueProbs.size)

      var i = 0
      while (i < quotientValues.size) {
        quotientValues(i) = valueProbs(i) * factor.valueProbs(i)
        i += 1
      }

      SingleTableFactor(varId, variableDim, quotientValues)
    }

    productForSingleFactor(factor.asInstanceOf[SingleTableFactor])
  }

  override def /(that: Factor): SingleTableFactor = {

    def divideForSingleFactor(factor: SingleTableFactor): SingleTableFactor = {
      require(varId == factor.varId, "Variable ids for quotient factors are not the same")
      require(variableDim == factor.variableDim, "Variable dimensions for quotient factors are not the same")

      val quotientValues = new Array[Double](valueProbs.size)

      var i = 0
      while (i < quotientValues.size) {
        if (valueProbs(i) == factor.valueProbs(i)) quotientValues(i) = 1
        else quotientValues(i) = valueProbs(i) / factor.valueProbs(i)
        i += 1
      }

      SingleTableFactor(varId, variableDim, quotientValues)
    }

    divideForSingleFactor(that.asInstanceOf[SingleTableFactor])
  }

  override def equals(that: Factor, threshold: Double): Boolean = {
    val tableFactor = that.asInstanceOf[SingleTableFactor]
    val notTheSame = valueProbs.zip(tableFactor.valueProbs).find { case (thisVal, thatVal) => thisVal.isNaN || thatVal.isNaN || abs(thisVal - thatVal) > threshold }
    notTheSame.isEmpty
  }

  override def toString() = "SingleTableFactor(%s,%s,%s)".format(varId, variableDim, valueProbs.toList)
}