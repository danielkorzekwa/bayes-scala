package dk.bayes.model.factorgraph

import org.junit._
import Assert._
import dk.bayes.model.factor.GaussianFactor
import dk.bayes.model.factor.LinearGaussianFactor
import dk.bayes.model.factor.api.Factor
import dk.bayes.model.factor.api.SingleFactor
import GenericFactorGraphTest._
import dk.bayes.model.factor.api.GenericFactor

class GenericFactorGraphTest {

  /**
   * Tests for addFactor
   */

  @Test def add_generic_factor {
    val factorGraph = GenericFactorGraph()

    val factor = TestGenericFactor(varIds = List(1, 2, 3))

    factorGraph.addFactor(factor)

    assertEquals(List(1, 2, 3), factorGraph.getVariables().sorted)
  }

  /**
   * Tests for getVariables()
   */
  @Test def getVariables_single_variable {
    val factorGraph = GenericFactorGraph()
    factorGraph.addFactor(GaussianFactor(varId = 10, m = 0, v = 1))

    assertEquals(List(10), factorGraph.getVariables())
  }

  @Test def getVariables_two_variables_across_two_factors {
    val factorGraph = GenericFactorGraph()
    factorGraph.addFactor(GaussianFactor(varId = 10, m = 0, v = 1))
    factorGraph.addFactor(LinearGaussianFactor(parentVarId = 10, varId = 20, a = 1, b = 0, v = 2))

    assertEquals(List(10, 20), factorGraph.getVariables().sorted)
  }

  /**
   * Tests for merge()
   */

  @Test(expected = classOf[IllegalArgumentException])
  def merge_two_factor_graphs_containing_the_same_variable {
    val factorGraph1 = GenericFactorGraph()
    factorGraph1.addFactor(GaussianFactor(varId = 10, m = 0, v = 1))
    factorGraph1.addFactor(LinearGaussianFactor(parentVarId = 10, varId = 20, a = 1, b = 0, v = 2))

    val factorGraph2 = GenericFactorGraph()
    factorGraph2.addFactor(GaussianFactor(varId = 20, m = 0, v = 1))

    factorGraph1.merge(factorGraph2)
  }

  @Test def merge_two_empty_factor_graphs {
    val factorGraph1 = GenericFactorGraph()
    val factorGraph2 = GenericFactorGraph()

    val mergedFactorGraph = factorGraph2.merge(factorGraph1)
    assertEquals(0, mergedFactorGraph.getNodes.size)
  }

  @Test def merge_non_empty_factor_graph_with_empty_factor_graph {

    val factorGraph1 = GenericFactorGraph()
    factorGraph1.addFactor(GaussianFactor(varId = 10, m = 0, v = 1))
    factorGraph1.addFactor(LinearGaussianFactor(parentVarId = 10, varId = 20, a = 1, b = 0, v = 2))

    val factorGraph2 = GenericFactorGraph()

    val mergedFactorGraph = factorGraph1.merge(factorGraph2)
    assertEquals(4, mergedFactorGraph.getNodes.size)

    assertEquals(GaussianFactor(varId = 10, m = 0, v = 1), mergedFactorGraph.getNodes()(0).asInstanceOf[FactorNode].getFactor())
    assertEquals(VarNode(10), mergedFactorGraph.getNodes()(1))

    assertEquals(LinearGaussianFactor(parentVarId = 10, varId = 20, a = 1, b = 0, v = 2), mergedFactorGraph.getNodes()(2).asInstanceOf[FactorNode].getFactor)
    assertEquals(VarNode(20), mergedFactorGraph.getNodes()(3))

  }

  @Test def merge_empty_factor_graph_with_non_empty_factor_graph {

    val factorGraph1 = GenericFactorGraph()
    factorGraph1.addFactor(GaussianFactor(varId = 10, m = 0, v = 1))
    factorGraph1.addFactor(LinearGaussianFactor(parentVarId = 10, varId = 20, a = 1, b = 0, v = 2))

    val factorGraph2 = GenericFactorGraph()

    val mergedFactorGraph = factorGraph2.merge(factorGraph1)
    assertEquals(4, mergedFactorGraph.getNodes.size)

    assertEquals(GaussianFactor(varId = 10, m = 0, v = 1), mergedFactorGraph.getNodes()(0).asInstanceOf[FactorNode].getFactor())
    assertEquals(VarNode(10), mergedFactorGraph.getNodes()(1))
    assertEquals(LinearGaussianFactor(parentVarId = 10, varId = 20, a = 1, b = 0, v = 2), mergedFactorGraph.getNodes()(2).asInstanceOf[FactorNode].getFactor())
    assertEquals(VarNode(20), mergedFactorGraph.getNodes()(3))

  }

  @Test def merge_two_factor_graphs {
    val factorGraph1 = GenericFactorGraph()
    factorGraph1.addFactor(GaussianFactor(varId = 10, m = 0, v = 1))

    val factorGraph2 = GenericFactorGraph()
    factorGraph2.addFactor(GaussianFactor(varId = 20, m = 0, v = 1))

    val mergedFactorGraph = factorGraph1.merge(factorGraph2)
    assertEquals(4, mergedFactorGraph.getNodes.size)

    assertEquals(GaussianFactor(varId = 10, m = 0, v = 1), mergedFactorGraph.getNodes()(0).asInstanceOf[FactorNode].getFactor())
    assertEquals(VarNode(10), mergedFactorGraph.getNodes()(1))
    assertEquals(GaussianFactor(varId = 20, m = 0, v = 1), mergedFactorGraph.getNodes()(2).asInstanceOf[FactorNode].getFactor())
    assertEquals(VarNode(20), mergedFactorGraph.getNodes()(3))
  }

}

object GenericFactorGraphTest {

  case class TestGenericFactor(varIds: Seq[Int]) extends GenericFactor {

    def getVariableIds(): Seq[Int] = varIds

    def marginal(marginalVarId: Int): SingleFactor = new GaussianFactor(marginalVarId, 0, 1)

    def outgoingMessages(msgsIn: Seq[SingleFactor]): Seq[SingleFactor] = throw new UnsupportedOperationException("Not implemented")

  }
}