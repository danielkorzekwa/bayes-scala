package dk.bayes.model.clustergraph

import org.junit._
import Assert._
import dk.bayes.model.clustergraph.ClusterGraph._
import dk.bayes.testutil.StudentBN
import dk.bayes.testutil.AssertUtil._
import dk.bayes.model.clustergraph.ClusterGraph._
import dk.bayes.testutil.AssertUtil._
import dk.bayes.testutil.StudentBN
import dk.bayes.model.clustergraph.factor._

class GenericClusterGraphTest {

import StudentBN._

  val clusterGraph = createStudentGraph()

  @Test(expected = classOf[IllegalArgumentException]) def add_edge_with_multiple_sepset_variables {
    clusterGraph.addEdge(3, 3)
  }

  @Test def getClusters {
    val clusters = clusterGraph.getClusters()

    assertEquals(5, clusters.size)

    assertEquals(5, clusters(0).id)
    assertFactor(letterFactor, clusters(0).getFactor())
    assertEdges(List(edge(5, 3, gradeVar)), clusters(0).getEdges())

    assertEquals(4, clusters(1).id)
    assertFactor(satFactor, clusters(1).getFactor())
    assertEdges(List(edge(4, 2, intelliVar)), clusters(1).getEdges())

    assertEquals(3, clusters(2).id)
    assertFactor(gradeFactor, clusters(2).getFactor())
    assertEdges(List(
      edge(3, 5, gradeVar),
      edge(3, 2, intelliVar),
      edge(3, 1, difficultyVar)), clusters(2).getEdges())

    assertEquals(2, clusters(3).id)
    assertFactor(intelliFactor, clusters(3).getFactor())
    assertEdges(List(
      edge(2, 4, intelliVar),
      edge(2, 3, intelliVar)), clusters(3).getEdges())

    assertEquals(1, clusters(4).id)
    assertFactor(difficultyFactor, clusters(4).getFactor())
    assertEdges(List(edge(1, 3, difficultyVar)), clusters(4).getEdges())

  }
  
  @Test def getVariables = assertEquals(List(Var(3, 3), Var(5, 2), Var(2, 2), Var(4, 2), Var(1, 2)), clusterGraph.getVariables())

  private def edge(srcClusterId: Int, destClusterId: Int, variable: Var): Edge = {
    val edge = Edge(destClusterId, variable)
    val linkedEdge = Edge(srcClusterId, variable)
    edge.setIncomingEdge(linkedEdge)
    linkedEdge.setIncomingEdge(edge)
    edge
  }

  private def assertEdges(expected: Seq[Edge], actual: Seq[Edge]) {

    assertEquals("Wrong number of edges:" + expected.size, expected.size, actual.size)

    var i = 0
    for ((expected, actual) <- expected.zip(actual)) {
      assertEquals("Element: " + i, expected.destClusterId, actual.destClusterId)

      assertEquals(expected.sepsetVariable, actual.sepsetVariable)
      assertFactor(expected.getOldMessage, actual.getOldMessage)
      assertFactor(expected.getNewMessage, actual.getNewMessage)

      i += 1
    }
  }
}