package dk.bayes.testutil.test

import org.junit._
import Assert._
import dk.bayes.infer.LoopyBP
import dk.bayes.testutil.TennisDBN._
import dk.bayes.testutil.AssertUtil._
import dk.bayes.model.clustergraph.factor._

class TennisDBNTest {

  val tennisGraph = createTennisClusterGraph()
  val loopyBP = LoopyBP(tennisGraph)

  @Test def marginal_for_grade_given_sat_is_high {

    loopyBP.setEvidence(match1v2Time0Var.id, 0)
    loopyBP.setEvidence(match1v2Time1Var.id, 0)
    loopyBP.setEvidence(match2v3Time1Var.id, 1)
    loopyBP.setEvidence(match1v3Time2Var.id, 1)
    loopyBP.setEvidence(match2v3Time2Var.id, 0)

    loopyBP.calibrate()

    val match1v2Time2Marginal = loopyBP.marginal(match1v2Time2Var.id)

    assertFactor(Factor(Var(12, 2), Array(0.5407, 0.4592)), match1v2Time2Marginal, 0.0001)
  }
}