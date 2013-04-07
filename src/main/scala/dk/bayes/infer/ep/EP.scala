package dk.bayes.infer.ep

import dk.bayes.model.factor.Factor

/**
 * Expectation Propagation Bayesian Inferece algorithm by Thomas Minka.
 *
 * Thomas P Minka. A family of algorithms for approximate Bayesian inference, 2001
 * Christopher M. Bishop. Pattern Recognition and Machine Learning (Information Science and Statistics), 2009
 *
 * @author Daniel Korzekwa
 */
trait EP {

  /**
   * Sets evidence in a factor graph.
   *
   * @param varId  Variable id
   * @param varValue Variable value
   */
  def setEvidence(varId: Int, varValue: AnyVal)

  /**
   * Calibrates factor graph.
   *
   */
  def calibrate()

  /**
   * Returns marginal factor for a given variable in a factor graph.
   */
  def marginal(variableId: Int): Factor
}