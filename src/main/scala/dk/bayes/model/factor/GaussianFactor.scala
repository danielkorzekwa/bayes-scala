package dk.bayes.model.factor

import dk.bayes.gaussian.Gaussian

/**
 * This class represents a factor for a Univariate Gaussian Distribution.
 *
 * @author Daniel Korzekwa
 *
 * @param varId Factor variable id
 * @param m Mean
 * @param v Variance
 */
case class GaussianFactor(varId: Int, m: Double, v: Double) extends Factor {

  def getVariableIds(): Seq[Int] = Vector(varId)

  def marginal(varId: Int): Factor = this.copy()

  def withEvidence(varId: Int, varValue: AnyVal): TableFactor = throw new UnsupportedOperationException("Not implemented yet")

  def getValue(assignment: (Int, AnyVal)*): Double = throw new UnsupportedOperationException("Not implemented yet")

  def *(factor: Factor): Factor = throw new UnsupportedOperationException("Not implemented yet")
}