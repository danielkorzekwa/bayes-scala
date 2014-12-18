package dk.bayes.infer

import org.junit._
import Assert._
import dk.bayes.testutil.StudentBN
import dk.bayes.model.clustergraph.GenericClusterGraph
import dk.bayes.testutil._
import dk.bayes.testutil.AssertUtil._
import dk.bayes.model.clustergraph.factor._
import dk.bayes.model.clustergraph.ClusterGraph
import StudentBN._

class LoopyBPStudentTest {

  val studentGraph = createStudentGraph()

  def progress(iterNum: Int) = println("Loopy BP iter= " + iterNum)

  val loopyBP = LoopyBP(studentGraph)

  /**
   * Tests for marginal() method
   */

  @Test def marginal {

    loopyBP.calibrate(progress)

    val difficultyMarginal = loopyBP.marginal(difficultyVar.id)
    val intelliMarginal = loopyBP.marginal(intelliVar.id)
    val gradeMarginal = loopyBP.marginal(gradeVar.id)
    val satMarginal = loopyBP.marginal(satVar.id)
    val letterMarginal = loopyBP.marginal(letterVar.id)

    assertFactor(Factor(Var(1, 2), Array(0.6, 0.4)), difficultyMarginal, 0.0001)
    assertFactor(Factor(Var(2, 2), Array(0.7, 0.3)), intelliMarginal, 0.0001)
    assertFactor(Factor(Var(3, 3), Array(0.3620, 0.2884, 0.3496)), gradeMarginal, 0.0001)
    assertFactor(Factor(Var(4, 2), Array(0.725, 0.275)), satMarginal, 0.0001)
    assertFactor(Factor(Var(5, 2), Array(0.4976, 0.5023)), letterMarginal, 0.0001)
  }

  @Test def marginal_given_sat_is_high {
    loopyBP.setEvidence(satVar.id, 0)

    loopyBP.calibrate(progress)

    val difficultyMarginal = loopyBP.marginal(difficultyVar.id)
    val intelliMarginal = loopyBP.marginal(intelliVar.id)
    val gradeMarginal = loopyBP.marginal(gradeVar.id)
    val satMarginal = loopyBP.marginal(satVar.id)
    val letterMarginal = loopyBP.marginal(letterVar.id)

    assertFactor(Factor(Var(1, 2), Array(0.6, 0.4)), difficultyMarginal, 0.0001)
    assertFactor(Factor(Var(2, 2), Array(0.9172, 0.0827)), intelliMarginal, 0.0001)
    assertFactor(Factor(Var(3, 3), Array(0.2446, 0.3257, 0.4295)), gradeMarginal, 0.0001)
    assertFactor(Factor(Var(4, 2), Array(1d, 0)), satMarginal, 0.0001)
    assertFactor(Factor(Var(5, 2), Array(0.58, 0.4199)), letterMarginal, 0.0001)

  }

  /**
   * Tests for clusterBelief() method
   */

  @Test def cluster_belief {

    loopyBP.calibrate(progress)

    val difficultyClusterBelief = loopyBP.clusterBelief(1)
    val intelliClusterBelief = loopyBP.clusterBelief(2)
    val gradeClusterBelief = loopyBP.clusterBelief(3)
    val satClusterBelief = loopyBP.clusterBelief(4)
    val letterClusterBelief = loopyBP.clusterBelief(5)

    assertFactor(Factor(Var(1, 2), Array(0.6, 0.4)), difficultyClusterBelief, 0.0001)
    assertFactor(Factor(Var(2, 2), Array(0.7, 0.3)), intelliClusterBelief, 0.0001)

    assertFactor(Factor(Var(2, 2), Var(1, 2), Var(3, 3),
      Array(0.1260, 0.1680, 0.1260, 0.0140, 0.0700, 0.1960, 0.1620, 0.0144, 0.0036, 0.0600, 0.0360, 0.0240)), gradeClusterBelief, 0.0001)

    assertFactor(Factor(Var(2, 2), Var(4, 2), Array(0.6650, 0.0350, 0.0600, 0.2400)), satClusterBelief, 0.0001)
    assertFactor(Factor(Var(3, 3), Var(5, 2), Array(0.0362, 0.3258, 0.1154, 0.1730, 0.3461, 0.0035)), letterClusterBelief, 0.0001)
  }

  @Test def cluster_belief_given_sat_is_high {
    loopyBP.setEvidence(satVar.id, 0)

    loopyBP.calibrate(progress)

    val difficultyClusterBelief = loopyBP.clusterBelief(1)
    val intelliClusterBelief = loopyBP.clusterBelief(2)
    val gradeClusterBelief = loopyBP.clusterBelief(3)
    val satClusterBelief = loopyBP.clusterBelief(4)
    val letterClusterBelief = loopyBP.clusterBelief(5)

    assertFactor(Factor(Var(1, 2), Array(0.6, 0.4)), difficultyClusterBelief, 0.0001)
    assertFactor(Factor(Var(2, 2), Array(0.9172, 0.0828)), intelliClusterBelief, 0.0001)

    assertFactor(Factor(Var(2, 2), Var(1, 2), Var(3, 3),
      Array(0.1651, 0.2201, 0.1651, 0.0183, 0.0917, 0.2568, 0.0447, 0.0040, 0.0010, 0.0166, 0.0099, 0.0066)), gradeClusterBelief, 0.0001)

    assertFactor(Factor(Var(2, 2), Var(4, 2), Array(0.9172, 0.0000, 0.0828, 0.0000)), satClusterBelief, 0.0001)
    assertFactor(Factor(Var(3, 3), Var(5, 2), Array(0.0245, 0.2202, 0.1303, 0.1955, 0.4252, 0.0043)), letterClusterBelief, 0.0001)

  }

  @Test def cluster_belief_given_full_evidence {
    loopyBP.setEvidence(difficultyVar.id, 0)
    loopyBP.setEvidence(intelliVar.id, 1)
    loopyBP.setEvidence(gradeVar.id, 0)
    loopyBP.setEvidence(satVar.id, 0)
    loopyBP.setEvidence(letterVar.id, 1)

    loopyBP.calibrate(progress)

    val difficultyClusterBelief = loopyBP.clusterBelief(1)
    val intelliClusterBelief = loopyBP.clusterBelief(2)
    val gradeClusterBelief = loopyBP.clusterBelief(3)
    val satClusterBelief = loopyBP.clusterBelief(4)
    val letterClusterBelief = loopyBP.clusterBelief(5)

    assertFactor(Factor(Var(1, 2), Array(1d, 0)), difficultyClusterBelief, 0.0001)
    assertFactor(Factor(Var(2, 2), Array(0d, 1)), intelliClusterBelief, 0.0001)

    assertFactor(Factor(Var(2, 2), Var(1, 2), Var(3, 3),
      Array(0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0)), gradeClusterBelief, 0.0001)

    assertFactor(Factor(Var(2, 2), Var(4, 2), Array(0, 0, 1, 0.0000)), satClusterBelief, 0.0001)
    assertFactor(Factor(Var(3, 3), Var(5, 2), Array(0, 1, 0, 0, 0, 0)), letterClusterBelief, 0.0001)
  }

  /**
   * Tests for logLikelihood() method
   */
  @Test(expected = classOf[IllegalArgumentException]) def logLikelihood_empty_assignment {
    loopyBP.logLikelihood(Array())
  }

  @Test(expected = classOf[IllegalArgumentException]) def logLikelihood_partial_assignment {

    val assignment = Array((1, 0), (2, 0))

    loopyBP.logLikelihood(assignment)
  }

  @Test(expected = classOf[IllegalArgumentException]) def logLikelihood_assignment_not_unique {

    val assignment = Array((1, 0), (2, 0), (1, 0), (3, 0))

    loopyBP.logLikelihood(assignment)
  }

  @Test def logLikelihood {
    val assignment = Array((1, 0), (2, 1), (3, 1), (4, 0), (5, 1))

    val llh = loopyBP.logLikelihood(assignment)

    assertEquals(-6.3607, llh, 0.0001)
  }

  /**
   * Tests for logLikelihood of evidence
   *
   */

  @Test def logLikelihood_of_sat_is_high {

    val logLikelihood = loopyBP.calibrateWithEvidence(List((satVar.id, 0)), progress)

    assertEquals(-0.32158, logLikelihood, 0.0001)

    val difficultyClusterBelief = loopyBP.clusterBelief(1)
    val intelliClusterBelief = loopyBP.clusterBelief(2)
    val gradeClusterBelief = loopyBP.clusterBelief(3)
    val satClusterBelief = loopyBP.clusterBelief(4)
    val letterClusterBelief = loopyBP.clusterBelief(5)

    assertFactor(Factor(Var(1, 2), Array(0.6, 0.4)), difficultyClusterBelief, 0.0001)
    assertFactor(Factor(Var(2, 2), Array(0.9172, 0.0828)), intelliClusterBelief, 0.0001)

    assertFactor(Factor(Var(2, 2), Var(1, 2), Var(3, 3),
      Array(0.1651, 0.2201, 0.1651, 0.0183, 0.0917, 0.2568, 0.0447, 0.0040, 0.0010, 0.0166, 0.0099, 0.0066)), gradeClusterBelief, 0.0001)

    assertFactor(Factor(Var(2, 2), Var(4, 2), Array(0.9172, 0.0000, 0.0828, 0.0000)), satClusterBelief, 0.0001)
    assertFactor(Factor(Var(3, 3), Var(5, 2), Array(0.0245, 0.2202, 0.1303, 0.1955, 0.4252, 0.0043)), letterClusterBelief, 0.0001)

  }

  @Test def logLikelihood_of_full_evidence {
    val evidence = List((difficultyVar.id, 0), (intelliVar.id, 1), (gradeVar.id, 0), (satVar.id, 0), (letterVar.id, 1))
    val logLikelihood = loopyBP.calibrateWithEvidence(evidence, progress)

    assertEquals(-3.5349, logLikelihood, 0.0001)
  }
}