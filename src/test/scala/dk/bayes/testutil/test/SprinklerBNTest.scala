package dk.bayes.testutil.test

import org.junit._
import Assert._
import dk.bayes.infer.LoopyBP
import dk.bayes.testutil.AssertUtil._
import dk.bayes.model.clustergraph.factor._
import dk.bayes.testutil.SprinklerBN._

class SprinklerBNTest {

  val sprinklerGraph = createSprinklerGraph()
  val loopyBP = LoopyBP(sprinklerGraph)

  @Test def marginal_for_slipperyRoad_given_sprinkler_and_wet_grass_are_true {

    loopyBP.setEvidence(2, 0)
    loopyBP.setEvidence(4, 0)

    loopyBP.calibrate()

    val slipperyRoadMarginal = loopyBP.marginal(slipperyRoadVar.id)

    assertFactor(Factor(Var(5, 2), Array(0.2249, 0.7750)), slipperyRoadMarginal, 0.0001)
  }
}