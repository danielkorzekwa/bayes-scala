package dk.bayes.model.factor

import dk.bayes.gaussian.Gaussian

/**
 * This class represents a factor for a Univariate Gaussian Distribution.
 *
 * @author Daniel Korzekwa
 *
 * @param varId Unique factor variable id
 * @param m Mean
 * @param v Variance
 */
case class GaussianFactor(varId: Int, m: Double, v: Double) extends Factor {

  def withEvidence(varId: Int, varValue: AnyVal): TableFactor = throw new UnsupportedOperationException("Not implemented yet")

  def getValue(assignment: (Int, AnyVal)*): Double = throw new UnsupportedOperationException("Not implemented yet")
}