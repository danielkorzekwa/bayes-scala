package dk.bayes.clustergraph.testutil.test

import org.junit._
import Assert._
import dk.bayes.clustergraph.infer.LoopyBP
import dk.bayes.clustergraph.factor.Var
import dk.bayes.clustergraph.factor.Factor
import dk.bayes.clustergraph.testutil.StudentBN._
import dk.bayes.clustergraph.testutil.AssertUtil._

class StudentBNTest {

  val studentGraph = createStudentGraph()
  val loopyBP = LoopyBP(studentGraph)

  @Test def marginal_for_grade_given_sat_is_high:Unit = {

    loopyBP.setEvidence(satVar.id, 0)

    loopyBP.calibrate()

    val gradeMarginal = loopyBP.marginal(gradeVar.id)

    assertFactor(Factor(Var(3, 3), Array(0.2446, 0.3257, 0.4295)), gradeMarginal, 0.0001)
  }
}