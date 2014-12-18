package dk.bayes.model.clustergraph.factor

import org.junit._

import Assert._

class FactorUtilTest {

  @Test def normalise {
    assertEquals(List(0.4, 0.6, 0), FactorUtil.normalise(Array(0.2, 0.3, 0)).toList)
  }

  @Test def assignmentToIndex {

    val assignment = Array(0, 1, 2)
    val stepSizes = Array(6, 2, 1)
    assertEquals(4, FactorUtil.assignmentToIndex(assignment, stepSizes))
  }

  @Test def calcStepSizes {
    val stepSizes = FactorUtil.calcStepSizes(Array(Var(1, 2), Var(2, 3), Var(2, 4)))
    assertEquals(List(12, 4, 1), stepSizes.toList)
  }
}