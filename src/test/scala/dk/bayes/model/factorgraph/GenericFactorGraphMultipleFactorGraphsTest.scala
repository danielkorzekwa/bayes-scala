package dk.bayes.model.factorgraph

import org.junit._
import Assert._
import dk.bayes.model.factor.GaussianFactor
import dk.bayes.model.factor.LinearGaussianFactor

/**
 * Tests for addFactor(factor: Factor, factorGraphs: Seq[FactorGraph]): Seq[FactorGraph] method.
 *
 */
class GenericFactorGraphMultipleFactorGraphsTest {

  @Test def factor_graphs_list_is_empty {

    val factor = GaussianFactor(varId = 10, m = 1, v = 0)

    val factorGraphs = GenericFactorGraph.addFactor(factor, factorGraphs = Nil)
    assertEquals(1, factorGraphs.size)

    val factorGraph = factorGraphs.head
    assertEquals(2, factorGraph.getNodes.size)
    assertEquals(factor, factorGraph.getNodes()(0).asInstanceOf[FactorNode].getFactor())
    assertEquals(VarNode(10), factorGraph.getNodes()(1))

  }

  @Test def no_factor_graph_for_new_factor {
    val factor = GaussianFactor(varId = 10, m = 1, v = 0)

    val oldFactorGraph = GenericFactorGraph()
    oldFactorGraph.addFactor(GaussianFactor(varId = 20, m = 1, v = 0))

    val newFactorGraphs = GenericFactorGraph.addFactor(factor, factorGraphs = List(oldFactorGraph))
    assertEquals(2, newFactorGraphs.size)

    val newFactorGraph1 = newFactorGraphs(0)
    assertEquals(2, newFactorGraph1.getNodes.size)
    assertEquals(factor, newFactorGraph1.getNodes()(0).asInstanceOf[FactorNode].getFactor())
    assertEquals(VarNode(10), newFactorGraph1.getNodes()(1))

    val newFactorGraph2 = newFactorGraphs(1)
    assertEquals(2, newFactorGraph2.getNodes.size)
    assertEquals(GaussianFactor(varId = 20, m = 1, v = 0), newFactorGraph2.getNodes()(0).asInstanceOf[FactorNode].getFactor())
    assertEquals(VarNode(20), newFactorGraph2.getNodes()(1))
  }

  @Test def factor_belongs_to_single_factor_graph {
    val factor = GaussianFactor(varId = 10, m = 1, v = 0)

    val oldFactorGraph1 = GenericFactorGraph()
    oldFactorGraph1.addFactor(GaussianFactor(varId = 20, m = 1, v = 0))

    val oldFactorGraph2 = GenericFactorGraph()
    oldFactorGraph2.addFactor(GaussianFactor(varId = 10, m = 1, v = 2))

    val newFactorGraphs = GenericFactorGraph.addFactor(factor, factorGraphs = List(oldFactorGraph1, oldFactorGraph2))
    assertEquals(2, newFactorGraphs.size)

    val newFactorGraph1 = newFactorGraphs(0)
    assertEquals(3, newFactorGraph1.getNodes.size)
    assertEquals(GaussianFactor(varId = 10, m = 1, v = 2), newFactorGraph1.getNodes()(0).asInstanceOf[FactorNode].getFactor())
    assertEquals(VarNode(10), newFactorGraph1.getNodes()(1))
    assertEquals(factor, newFactorGraph1.getNodes()(2).asInstanceOf[FactorNode].getFactor())

    val newFactorGraph2 = newFactorGraphs(1)
    assertEquals(2, newFactorGraph2.getNodes.size)
    assertEquals(GaussianFactor(varId = 20, m = 1, v = 0), newFactorGraph2.getNodes()(0).asInstanceOf[FactorNode].getFactor())
    assertEquals(VarNode(20), newFactorGraph2.getNodes()(1))
  }

  @Test def factor_belongs_to_two_factor_graphs {

    val factor = LinearGaussianFactor(parentVarId = 10, varId = 20, a = 1, b = 0, v = 2)

    val oldFactorGraph1 = GenericFactorGraph()
    oldFactorGraph1.addFactor(GaussianFactor(varId = 20, m = 3, v = 8))

    val oldFactorGraph2 = GenericFactorGraph()
    oldFactorGraph2.addFactor(GaussianFactor(varId = 10, m = 5, v = 2))

    val newFactorGraphs = GenericFactorGraph.addFactor(factor, factorGraphs = List(oldFactorGraph1, oldFactorGraph2))
    assertEquals(1, newFactorGraphs.size)

    val newFactorGraph = newFactorGraphs(0)
    assertEquals(5, newFactorGraph.getNodes.size)
    assertEquals(GaussianFactor(varId = 20, m = 3, v = 8), newFactorGraph.getNodes()(0).asInstanceOf[FactorNode].getFactor())
    assertEquals(VarNode(20), newFactorGraph.getNodes()(1))
    assertEquals(GaussianFactor(varId = 10, m = 5, v = 2), newFactorGraph.getNodes()(2).asInstanceOf[FactorNode].getFactor())
    assertEquals(VarNode(10), newFactorGraph.getNodes()(3))
    assertEquals(factor, newFactorGraph.getNodes()(4).asInstanceOf[FactorNode].getFactor())
  }

}