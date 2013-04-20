TrueSkill on a factor graph in Tennis (Dynamic Bayesian Network)
=============================================================================================================

Dynamic Bayesian Network for 3 players with 6 games over 3 time slices.

![TruSkill Tennis DBN](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/trueskill_in_tennis_factor_graph_dbn/trueskill_tennis_dbn.png "TruSkill Tennis DBN")


Create factor variables:

	//Create player skill variables
	val player1Time0VarId = 1
	val player1Time1VarId = 2
	val player1Time2VarId = 3
	
	val player2Time0VarId = 4
	val player2Time1VarId = 5
	val player2Time2VarId = 6
	
	val player3Time1VarId = 7
	val player3Time2VarId = 8
	
	//Create match outcome variables
	val match1v2Time0VarId = 9
	val match1v2Time1VarId = 10
	val match2v3Time1VarId = 11
	val match1v2Time2VarId = 12
	val match1v3Time2VarId = 13
	val match2v3Time2VarId = 14

Create factor graph and add skill factors:

	val factorGraph = GenericFactorGraph()
	
	factorGraph.addFactor(GaussianFactor(player1Time0VarId, 4, 81))
	factorGraph.addFactor(LinearGaussianFactor(player1Time0VarId, player1Time1VarId, 1, 0, pow(25d / 6, 2)))
	factorGraph.addFactor(LinearGaussianFactor(player1Time1VarId, player1Time2VarId, 1, 0, pow(25d / 6, 2)))
	factorGraph.addFactor(GaussianFactor(player2Time0VarId, 4, 81))
	factorGraph.addFactor(LinearGaussianFactor(player2Time0VarId, player2Time1VarId, 1, 0, pow(25d / 6, 2)))
	factorGraph.addFactor(LinearGaussianFactor(player2Time1VarId, player2Time2VarId, 1, 0, pow(25d / 6, 2)))
	factorGraph.addFactor(GaussianFactor(player3Time1VarId, 4, 81))
	factorGraph.addFactor(LinearGaussianFactor(player3Time1VarId, player3Time2VarId, 1, 0, pow(25d / 6, 2)))

Add factors to factor graph for player performance, performance difference and match outcome:

	val varId = new AtomicInteger(15)

	def addTennisGameToFactorGraph(player1VarId: Int, player2VarId: Int, matchVarId: Int) {
	
	  val perf1VarId = varId.getAndIncrement()
	  val perf2VarId = varId.getAndIncrement()
	  val perfDiffVarId = varId.getAndIncrement()
	
	  factorGraph.addFactor(LinearGaussianFactor(player1VarId, perf1VarId, 1, 0, pow(25d / 6, 2)))
	  factorGraph.addFactor(LinearGaussianFactor(player2VarId, perf2VarId, 1, 0, pow(25d / 6, 2)))
	  factorGraph.addFactor(DiffGaussianFactor(perf1VarId, perf2VarId, perfDiffVarId))
	  factorGraph.addFactor(TruncGaussianFactor(perfDiffVarId, matchVarId, 0))
	}
	
	addTennisGameToFactorGraph(player1Time0VarId, player2Time0VarId, match1v2Time0VarId)
	
	addTennisGameToFactorGraph(player1Time1VarId, player2Time1VarId, match1v2Time1VarId)
	addTennisGameToFactorGraph(player2Time1VarId, player3Time1VarId, match2v3Time1VarId)
	
	addTennisGameToFactorGraph(player1Time2VarId, player2Time2VarId, match1v2Time2VarId)
	addTennisGameToFactorGraph(player1Time2VarId, player3Time2VarId, match1v3Time2VarId)
	addTennisGameToFactorGraph(player2Time2VarId, player3Time2VarId, match2v3Time2VarId)

Set evidence: p1_p2_time_0 (p1 wins), p2_p3_time_1 (p2 wins) and calibrate the factor graph:

	val tennisFactorGraph = createTennisFactorGraph()
	val ep = GenericEP(tennisFactorGraph)
	ep.setEvidence(match1v2Time0VarId, 0)
	ep.setEvidence(match2v3Time1VarId, 0)
    
	ep.calibrate(30, progress)

Get marginals:

	val outcomeMarginal_t1 = ep.marginal(match1v2Time1VarId)
	assertEquals(0.6590, outcomeMarginal_t1.getValue((match1v2Time1VarId, 0)), 0.0001)
	assertEquals(0.3409, outcomeMarginal_t1.getValue((match1v2Time1VarId, 1)), 0.0001)
	
	val outcomeMarginal_t2 = ep.marginal(match1v2Time2VarId)
	assertEquals(0.6449, outcomeMarginal_t2.getValue((match1v2Time2VarId, 0)), 0.0001)
	assertEquals(0.3550, outcomeMarginal_t2.getValue((match1v2Time2VarId, 1)), 0.0001)
	
	val skill1_t0_marginal = ep.marginal(player1Time0VarId).asInstanceOf[GaussianFactor]
	assertEquals(10.2143, skill1_t0_marginal.m, 0.0001)
	assertEquals(54.6463, skill1_t0_marginal.v, 0.0001)
	
	val skill1_t1_marginal = ep.marginal(player1Time1VarId).asInstanceOf[GaussianFactor]
	assertEquals(10.2143, skill1_t1_marginal.m, 0.0001)
	assertEquals(72.0075, skill1_t1_marginal.v, 0.0001)
	
	val skill1_t2_marginal = ep.marginal(player1Time2VarId).asInstanceOf[GaussianFactor]
	assertEquals(10.2143, skill1_t2_marginal.m, 0.0001)
	assertEquals(89.3686, skill1_t2_marginal.v, 0.0001)
	
	val skill2_t0_marginal = ep.marginal(player2Time0VarId).asInstanceOf[GaussianFactor]
	assertEquals(3.7426, skill2_t0_marginal.m, 0.0001)
	assertEquals(44.8959, skill2_t0_marginal.v, 0.0001)
	
	val skill2_t1_marginal = ep.marginal(player2Time1VarId).asInstanceOf[GaussianFactor]
	assertEquals(5.0194, skill2_t1_marginal.m, 0.0001)
	assertEquals(53.8643, skill2_t1_marginal.v, 0.0001)
	
	val skill2_t2_marginal = ep.marginal(player2Time2VarId).asInstanceOf[GaussianFactor]
	assertEquals(5.0194, skill2_t2_marginal.m, 0.0001)
	assertEquals(71.2254, skill2_t2_marginal.v, 0.0001)

* [Scala code example](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/infer/ep/TrueSkillDBNTennisEPTest.scala)