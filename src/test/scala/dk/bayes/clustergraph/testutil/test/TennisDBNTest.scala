package dk.bayes.clustergraph.testutil.test

import org.junit._
import Assert._
import dk.bayes.clustergraph.infer.LoopyBP
import dk.bayes.clustergraph.factor.Var
import dk.bayes.clustergraph.factor.Factor
import dk.bayes.clustergraph.testutil.TennisDBN._
import dk.bayes.clustergraph.testutil.AssertUtil._

class TennisDBNTest {

  val tennisGraph = createTennisClusterGraph()
  val loopyBP = LoopyBP(tennisGraph)

  @Test def marginal_for_grade_given_sat_is_high:Unit = {

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