package dk.bayes.clustergraph.testutil.test

import org.junit._
import Assert._
import dk.bayes.clustergraph.infer.LoopyBP
import dk.bayes.clustergraph.factor.Var
import dk.bayes.clustergraph.factor.Factor
import dk.bayes.clustergraph.testutil.SprinklerBN._
import dk.bayes.clustergraph.testutil.AssertUtil._

class SprinklerBNTest {

  val sprinklerGraph = createSprinklerGraph()
  val loopyBP = LoopyBP(sprinklerGraph)

  @Test def marginal_for_slipperyRoad_given_sprinkler_and_wet_grass_are_true:Unit = {

    loopyBP.setEvidence(2, 0)
    loopyBP.setEvidence(4, 0)

    loopyBP.calibrate()

    val slipperyRoadMarginal = loopyBP.marginal(slipperyRoadVar.id)

    assertFactor(Factor(Var(5, 2), Array(0.2249, 0.7750)), slipperyRoadMarginal, 0.0001)
  }
}