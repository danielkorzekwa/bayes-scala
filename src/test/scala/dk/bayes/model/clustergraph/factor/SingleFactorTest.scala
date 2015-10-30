package dk.bayes.model.clustergraph.factor

import org.junit._
import Assert._
import Factor._
import dk.bayes.testutil.AssertUtil._

class SingleFactorTest {

  val factor = new SingleFactor(Var(1, 3), Array(0.3, 0.5, 0.2))

  /**
   * Tests for constructor
   */

  @Test(expected = classOf[IllegalArgumentException]) def create_values_inconsistent_with_dim_size:Unit = {
    SingleFactor(Var(1, 2), Array(0.3, 0.5, 0.2))
  }

  /**
   * Tests for getVariable() and getVariables() methods.
   */

  @Test def getVariable = assertEquals(Var(1, 3), factor.getVariable)

  @Test def getVariables = assertEquals(Array(Var(1, 3)).toList, factor.getVariables.toList)

  /**
   * Tests for getValue() and getValues() methods.
   */

  @Test(expected = classOf[IllegalArgumentException]) def getValue_empty_assignment:Unit = {
    factor.getValue(Array())
  }

  @Test(expected = classOf[IllegalArgumentException]) def getValue_multiple_assignments:Unit = {
    factor.getValue(Array(1, 2))
  }

  @Test(expected = classOf[ArrayIndexOutOfBoundsException]) def getValue_assignment_value_out_of_range:Unit = {
    factor.getValue(Array(3))
  }

  @Test def getValue = assertEquals(0.2, factor.getValue(Array(2)), 0)

  @Test def getValues = assertEquals(Array(0.3, 0.5, 0.2).toList, factor.getValues.toList)

  /**
   * Tests for product() method.
   */
  @Test(expected = classOf[IllegalArgumentException]) def product_factor_variable_ids_not_consistent:Unit = {
    val thatFactor = SingleFactor(Var(2, 3), Array(0.3, 0.5, 0.2))
    factor.product(thatFactor)
  }

  @Test(expected = classOf[IllegalArgumentException]) def product_factor_variable_dimensions_not_consistent:Unit = {
    val thatFactor = SingleFactor(Var(1, 2), Array(0.3, 0.5))
    factor.product(thatFactor)
  }

  @Test def product:Unit = {
    val thatFactor = new SingleFactor(Var(1, 3), Array(0.4, 0.1, 0.5))
    val factorProduct = factor.product(thatFactor)

    assertFactor(SingleFactor(Var(1, 3), Array(0.12, 0.05, 0.1)), factorProduct)
  }

  /**
   * Tests for withEvidence() method.
   */
  @Test(expected = classOf[IllegalArgumentException]) def withEvidence_variable_not_found:Unit = {
    factor.withEvidence(5, 1)
  }

  @Test(expected = classOf[ArrayIndexOutOfBoundsException]) def withEvidence_variable_value_index_out_of_range:Unit = {
    factor.withEvidence(1, 3)
  }

  @Test def withEvidence:Unit = {

    val factorWithEvidence = factor.withEvidence(1, 2)

    assertFactor(SingleFactor(Var(1, 3), Array(0, 0, 0.2)), factorWithEvidence)
  }

  /**
   * Tests for marginal() method.
   */

  @Test(expected = classOf[IllegalArgumentException]) def marginal_variable_not_found:Unit = {
    factor.marginal(5)
  }

  @Test def marginal = assertFactor(factor, factor.marginal(1))

  /**
   * Tests for normalise() method.
   */

  @Test def normalise_already_normalised:Unit = {

    val factor = SingleFactor(Var(1, 3), Array(0.3, 0.5, 0.2))
    val normalisedFactor = factor.normalise()

    assertFactor(factor, normalisedFactor)
  }

  @Test def normalise:Unit = {

    val factor = SingleFactor(Var(1, 3), Array(0.3, 0, 0.2))
    val normalisedFactor = factor.normalise()

    assertFactor(SingleFactor(Var(1, 3), Array(0.6, 0, 0.4)), normalisedFactor)
  }

  /**
   * Tests for mapAssignments
   */

  @Test def mapAssignments:Unit = {
    val factor = SingleFactor(Var(1, 3), Array(0.3, 0.5, 0.2))

    val assignments = factor.mapAssignments(a => a(0) + 5)

    assertEquals(List(5, 6, 7), assignments.toList)
  }
}