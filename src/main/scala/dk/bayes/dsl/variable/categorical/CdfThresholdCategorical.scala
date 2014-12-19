package dk.bayes.dsl.variable.categorical

import dk.bayes.dsl.Variable
import dk.bayes.dsl.variable.Gaussian

/**
 * Categorical variable with Gaussian parent
 *
 * P(y=1|x) = x_cdf(threshold)
 * P(y=0|x) = 1 - P(y=1|x)
 *
 * @author Daniel Korzekwa
 */
class CdfThresholdCategorical(val x: Gaussian, val cdfThreshold: Double,val value:Option[Int]) extends Variable {

  def getParents(): Seq[Gaussian] = Vector(x)
}