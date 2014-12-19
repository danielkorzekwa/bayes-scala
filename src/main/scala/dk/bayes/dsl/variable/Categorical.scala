package dk.bayes.dsl.variable

import dk.bayes.dsl.InferEngine
import dk.bayes.model.clustergraph.factor.Var
import dk.bayes.dsl.Variable
import dk.bayes.model.clustergraph.ClusterGraph
import dk.bayes.infer.LoopyBP
import dk.bayes.infer._
import java.util.concurrent.atomic.AtomicInteger
import dk.bayes.model.clustergraph.factor.Factor
import dk.bayes.dsl.variable.categorical.CdfThresholdCategorical
import dk.bayes.dsl.variable.categorical.infer.inferEngineCategorical

/**
 * Categorical variable (http://en.wikipedia.org/wiki/Categorical_distribution)
 *
 * @author Daniel Korzekwa
 */

class Categorical(val parents: Seq[Categorical], val cpd: Seq[Double]) extends Variable {

  val dim: Int = cpd.size / parents.map(p => p.dim).product
  private var value: Option[Int] = None

  def setValue(v: Int) { value = Some(v) }
  def getValue(): Option[Int] = value

  def getParents(): Seq[Categorical] = parents
}

object Categorical {

  /**
   * Constructors for categorical variables with categorical parents.
   */

  def apply(cpd: Seq[Double]) = new Categorical(Vector(), cpd)

  def apply(parent: Categorical, cpd: Seq[Double]): Categorical = new Categorical(Vector(parent), cpd)

  def apply(parent1: Categorical, parent2: Categorical, cpd: Seq[Double]): Categorical = new Categorical(Vector(parent1, parent2), cpd)

  /**
   * Constructor for categorical variable with Gaussian parent
   *
   * P(y=1|x) = x_cdf(threshold)
   * P(y=0|x) = 1 - P(y=1|x)
   */
  def apply(x: Gaussian, cdfThreshold: Double, value: Int): CdfThresholdCategorical = new CdfThresholdCategorical(x, cdfThreshold, Some(value))
  def apply(x: Gaussian, cdfThreshold: Double): CdfThresholdCategorical = new CdfThresholdCategorical(x, cdfThreshold, None)

  /**
   * Set up the inference engine for Categorical variable.
   */
  implicit val inferEngine = Vector(inferEngineCategorical)

}