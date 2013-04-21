TrueSkill on a factor graph in Tennis
=============================================================================================

Inference of player skills for a single tennis match. [(Scala code example)](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/infer/ep/TrueSkillOnlineTennisEPTest.scala)


![Single tennis game](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/trueskill_in_tennis_factor_graph/tennis_trueskill_bn.png "Single tennis game")

Create factor graph:

	val skill1VarId = 1
	val skill2VarId = 2
	val perf1VarId = 3
	val perf2VarId = 4
	val perfDiffVarId = 5
	val outcomeVarId = 6
	
	val skill1Factor = GaussianFactor(skill1VarId, 4, 81)
	val skill2Factor = GaussianFactor(skill2VarId, 41, 25)
	val perf1Factor = LinearGaussianFactor(skill1VarId, perf1VarId, 1, 0, pow(25d / 6, 2))
	val perf2Factor = LinearGaussianFactor(skill2VarId, perf2VarId, 1, 0, pow(25d / 6, 2))
	val perfDiffFactor = DiffGaussianFactor(perf1VarId, perf2VarId, perfDiffVarId)
	val outcomeFactor = TruncGaussianFactor(perfDiffVarId, outcomeVarId, 0)
	  
	val factorGraph = GenericFactorGraph()
	
	factorGraph.addFactor(skill1Factor)
	factorGraph.addFactor(skill2Factor)
	factorGraph.addFactor(perf1Factor)
	factorGraph.addFactor(perf2Factor)
	factorGraph.addFactor(perfDiffFactor)
	factorGraph.addFactor(outcomeFactor)

Set evidence (player 1 is a winner) and calibrate the factor graph:

	val tennisFactorGraph = createTennisFactorGraph()
	val ep = GenericEP(tennisFactorGraph)
	
	ep.setEvidence(outcomeVarId, 0)
	ep.calibrate(7, progress)

Get marginals for player skills and game outcome:

	val outcomeMarginal = ep.marginal(outcomeVarId)
	assertEquals(1, outcomeMarginal.getValue((outcomeVarId, 0)), 0.0001)
	assertEquals(0, outcomeMarginal.getValue((outcomeVarId, 1)), 0.0001)
	
	val skill1Marginal = ep.marginal(skill1VarId).asInstanceOf[GaussianFactor]
	assertEquals(27.1744, skill1Marginal.m, 0.0001)
	assertEquals(37.4973, skill1Marginal.v, 0.0001)
	
	val skill2Marginal = ep.marginal(skill2VarId).asInstanceOf[GaussianFactor]
	assertEquals(33.8473, skill2Marginal.m, 0.0001)
	assertEquals(20.8559, skill2Marginal.v, 0.0001)
