Bayesian Networks in Scala
===========
Collection of Bayesian algorithms for model representation, inference and learning.

**Major features**

* Model representation
  * factors: discrete, gaussian, linear gaussian, difference of gaussians, truncated gaussian
  * cluster graph
  * factor graph
  * gaussians: Univariate, Multivariate, Canonical and Linear Gaussians
* Inference (discrete, continuous and hybrid Bayesian Networks)
  * Loopy Belief Propagation in a cluster graph
  * Expectation Propagation on a factor graph
  * Manual inference using supported factor operations (product, divide, marginal, evidence)
  * Gaussian Process Regression (Gaussian likelihood and zero mean functions only)
* Learning parameters
  * Expectation Maximisation - Discrete Bayesian Networks. CPT Factors, Complete/Incomplete data, BN and Unrolled DBN
  * Learning parameters of Linear Dynamical Systems
  
**Roadmap**

None

**Documenation**

*   Discrete Bayesian Networks
    * [Getting Started - Loopy Belief Propagation in a Cluster Graph](#getting-started---loopy-belief-propagation-in-a-cluster-graph-1)
    * [Getting Started - Learning parameters with Expectation Maximisation in Bayesian Networks from incomplete data](#getting-started---learning-parameters-with-expectation-maximisation-in-bayesian-networks-from-incomplete-data--1)
    * [Getting Started - Learning parameters with Expectation Maximisation in Unrolled Dynamic Bayesian Networks from incomplete data](#getting-started---learning-parameters-with-expectation-maximisation-in-unrolled-dynamic-bayesian-networks-from-incomplete-data--1)
*   Continuous Bayesian Networks
    * [Linear Gaussian Models - 1D localisation, 4 approaches: Canonical Gaussian, Bayes's theorem for Gaussian Variables, Expectation Propagation and Kalman Filter](https://github.com/danielkorzekwa/bayes-scala/blob/master/doc/localisation_example/localisation_example.md)
*   Hybrid Bayesian Networks
    * [Gaussian approximation with moment matching, aka proj() operator in Expectation Propagation](https://github.com/danielkorzekwa/bayes-scala/blob/master/doc/moment_matching/moment_matching.md)
    * [Expectation Propagation for the Clutter Problem](https://github.com/danielkorzekwa/bayes-scala/blob/master/doc/clutter_problem_ep/clutter_problem_ep.md)
    * [TrueSkill - Updating player skills in tennis with Expectation Propagation inference algorithm](https://github.com/danielkorzekwa/bayes-scala/blob/master/doc/trueskill_in_tennis/trueskill_in_tennis.md)
    * [TrueSkill on a factor graph in Tennis] (https://github.com/danielkorzekwa/bayes-scala/blob/master/doc/trueskill_in_tennis_factor_graph/trueskill_in_tennis_factor_graph.md)
    * [TrueSkill on a factor graph in Tennis (Dynamic Bayesian Network for 3 players with 6 games over 3 time slices)] (https://github.com/danielkorzekwa/bayes-scala/blob/master/doc/trueskill_in_tennis_factor_graph_dbn/trueskill_in_tennis_factor_graph_dbn.md)
* Code examples only
    * [Linear Dynamical Systems M-step (prior mean, emission variance, transition variance)](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/learn/lds/GenericLDSMStepTest.scala)
    * [Linear Dynamical Systems EM (learning prior mean and emission variance only from multiple data sequences)](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/learn/lds/GenericLDSMStepTest.scala)
    * [Gaussian Process regression](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/infer/gp/GenericGPRegressionTest.scala)
                                 
*   Others 
    * [Plotting gaussians](https://github.com/danielkorzekwa/bayes-scala/blob/master/doc/plotting_gaussian/plotting_gaussian.md)
    * [Resources for Bayesian Networks](https://github.com/danielkorzekwa/bayes-scala/blob/master/doc/bn_resources/bn_resources.md)

Getting Started - Loopy Belief Propagation in a Cluster Graph [1](#references)
---------------

Consider the following example of a Bayesian Network [1](#references), created with SamIam tool [2](#references).

![Student Bayesian Network](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/student_bn.png "Student Bayesian Network")

### Example 1: Create cluster graph and compute marginal for Grade variable ([source code](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/infer/LoopyBPGettingStarted.scala))

Create cluster graph:

	//Create variables
	val difficultyVar = Var(1, 2)
	val intelliVar = Var(2, 2)
	val gradeVar = Var(3, 3)
	val satVar = Var(4, 2)
	val letterVar = Var(5, 2)
	
	//Create factors
	val difficultyFactor = Factor(difficultyVar, Array(0.6, 0.4))
	val intelliFactor = Factor(intelliVar, Array(0.7, 0.3))
	val gradeFactor = Factor(intelliVar, difficultyVar, gradeVar, Array(0.3, 0.4, 0.3, 0.05, 0.25, 0.7, 0.9, 0.08, 0.02, 0.5, 0.3, 0.2))
	val satFactor = Factor(intelliVar, satVar, Array(0.95, 0.05, 0.2, 0.8))
	val letterFactor = Factor(gradeVar, letterVar, Array(0.1, 0.9, 0.4, 0.6, 0.99, 0.01))
	
	//Create cluster graph
	val clusterGraph = GenericClusterGraph()
	clusterGraph.addCluster(1, difficultyFactor)
	clusterGraph.addCluster(2, intelliFactor)
	clusterGraph.addCluster(3, gradeFactor)
	clusterGraph.addCluster(4, satFactor)
	clusterGraph.addCluster(5, letterFactor)
	
	//Add edges between clusters in a cluster graph
	clusterGraph.addEdges((1, 3), (2, 3), (2, 4), (3, 5))

Calibrate cluster graph and get Grade marginal:
	
	//Calibrate cluster graph
	val loopyBP = LoopyBP(clusterGraph)
	loopyBP.calibrate()
	 
	//Get marginal for Grade variable
	val gradeMarginal = loopyBP.marginal(gradeVar.id)
	gradeMarginal.getVariables() // Var(3,3)
	gradeMarginal.getValues() // List(0.3620, 0.2884, 0.3496)

### Example 2: Compute marginal for Grade variable given SAT test is high ([source code](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/infer/LoopyBPGettingStarted.scala))

Set evidence for SAT variable and compute marginal for Grade variable:

	loopyBP.setEvidence(satVar.id, 0)
	loopyBP.calibrate()
	
	val gradeMarginal = loopyBP.marginal(gradeVar.id)
	gradeMarginal.getVariables() // Var(3,3)
	gradeMarginal.getValues() // List(0.2446, 0.3257, 0.4295)

Getting Started - Learning parameters with Expectation Maximisation in Bayesian Networks from incomplete data  [1](#references)
---------------

In this example we learn parameters of a Sprinkler Bayesian Network [3](#references). ([source code](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/learn/em/EMLearnSprinklerGettingStarted.scala))

![Sprinkler Bayesian Network](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/sprinkler_bn.png "Sprinkler Bayesian Network")

Create Sprinkler Network with table CPT parameters:

	//Create variables
	val winterVar = Var(1, 2)
	val sprinklerVar = Var(2, 2)
	val rainVar = Var(3, 2)
	val wetGrassVar = Var(4, 2)
	val slipperyRoadVar = Var(5, 2)
	
	//Create factors
	val winterFactor = Factor(winterVar, Array(0.2, 0.8))
	val sprinklerFactor = Factor(winterVar, sprinklerVar, Array(0.6, 0.4, 0.55, 0.45))
	val rainFactor = Factor(winterVar, rainVar, Array(0.1, 0.9, 0.3, 0.7))
	val wetGrassFactor = Factor(sprinklerVar, rainVar, wetGrassVar, Array(0.85, 0.15, 0.3, 0.7, 0.35, 0.65, 0.55, 0.45))
	val slipperyRoadFactor = Factor(rainVar, slipperyRoadVar, Array(0.5, 0.5, 0.4, 0.6))

	//Create cluster graph	
	val sprinklerGraph = ClusterGraph()

	sprinklerGraph.addCluster(winterVar.id, winterFactor)
	sprinklerGraph.addCluster(sprinklerVar.id, sprinklerFactor)
	sprinklerGraph.addCluster(rainVar.id, rainFactor)
	sprinklerGraph.addCluster(wetGrassVar.id, wetGrassFactor)
	sprinklerGraph.addCluster(slipperyRoadVar.id, slipperyRoadFactor)
	
	sprinklerGraph.addEdges((1, 2), (1, 3), (2, 4), (3, 4), (3, 5))

Learn parameters of Sprinkler Network from samples ([sprinkler_10k_samples_5pct_missing_values.dat](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/resources/sprinkler_data/sprinkler_10k_samples_5pct_missing_values.dat)):

	val maxIterNum = 5
	val variableIds = Array(winterVar.id, rainVar.id, sprinklerVar.id, slipperyRoadVar.id, wetGrassVar.id)
	val dataSet = DataSet.fromFile("src/test/resources/sprinkler_data/sprinkler_10k_samples_5pct_missing_values.dat", variableIds)
	
	GenericEMLearn.learn(sprinklerGraph, dataSet, maxIterNum)
    
	sprinklerGraph.getCluster(winterVar.id).getFactor() //Factor(winterVar, Array(0.6086, 0.3914))
	sprinklerGraph.getCluster(sprinklerVar.id).getFactor() //Factor(winterVar, sprinklerVar, Array(0.2041, 0.7958, 0.7506, 0.2493))
	sprinklerGraph.getCluster(rainVar.id).getFactor() //Factor(winterVar, rainVar, Array(0.8066, 0.1933, 0.0994, 0.9005))
	sprinklerGraph.getCluster(wetGrassVar.id).getFactor() //Factor(sprinklerVar, rainVar, wetGrassVar, Array(0.9481, 0.0518, 0.9052, 0.0947, 0.7924, 0.2075, 0.00001, 0.9999))
	sprinklerGraph.getCluster(slipperyRoadVar.id).getFactor() //Factor(rainVar, slipperyRoadVar, Array(.6984, 0.3015, 0.00001, 0.9999))

Getting Started - Learning parameters with Expectation Maximisation in Unrolled Dynamic Bayesian Networks from incomplete data  [1](#references)
---------------

For this scenario we learn parameters in a Dynamic Bayesian Network designed for predicting outcomes of tennis matches.
 
There are two types of variables in this network:

<table>
  <tbody>
    <tr>
      <th>Variable Type</th>
      <th>Values</th>
      <th>Description</th>
     
    </tr>
    <tr>
      <td>Player Skill</td>
      <td>High, Medium, Low</td>
      <td>How good player is at tennis</td>
    </tr>
     <tr>
      <td>Match Outcome</td>
      <td>Win, Loss</td>
      <td>Outcome of tennis match between two players</td>
    </tr>
  </tbody>
</table>

We name Player Skills as Hidden variables, because those are never observed and we try to infer their values over the time for all tennis players.
Match Outcome variables are always observed, given historical tennis results are available to us. 

Tennis network is sliced by time. e.g. weeks. Within a single time slice, tennis matches are represented by Match Outcome variables, whereas players are characterised by Player Skill variables. 
For better understanding of this structure, look at diagram below, which reflects the following sequence of tennis matches.

	player_id_1, player_id_2, player_1_won (won,lost), time
	1,2,won,0
	1,2,won,1
	2,3,lost,1
	1,2,?,2 //This game has not been played yet and we would like to infer the likelihood of its outcome. 
	1,3,lost,2
	2,3,won,2

![Tennis Dynamic Bayesian Network](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/tennis_dbn.png "Tennis Dynamic Bayesian Network")

Probability distribution of tennis data is modelled with prior, emission and transition parameters:

<table>
  <tbody>
    <tr>
      <th>Parameter Type</th>
      <th>Description</th>
    </tr>
    <tr>
      <td>Prior</td>
      <td>Initial player skill, at the time of his very first tennis game included in tennis training data</td>
    </tr>
     <tr>
      <td>Emission</td>
      <td>Probability of winning a tennis game given skills of both players</td>  
    </tr>
     <tr>
      <td>Transition</td>
      <td>Probability of changing player skills between time slices</td>  
    </tr>
  </tbody>
</table>

Those parameters are shared by corresponding variables, for example emission parameter is shared by all Match Outcome variables.
The following figure presents initial guess about network parameters, which we will learn from historical data applying Expectation Maximisation algorithm.

![Parameters for Tennis Dynamic Bayesian Network](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/tennis_dbn_parameters.png "Parameters for Tennis Dynamic Bayesian Network")

In the reminder of this tutorial we build cluster graph for Tennis Network and learn its prior, emission and transition parameters.

Create cluster graph for Tennis Network ([source code](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/testutil/TennisDBN.scala)):

	val tennisClusterGraph = createTennisClusterGraph()
	 
Learn parameters of Tennis Network from samples ([source code](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/learn/em/EMLearnTennisGettingStarted.scala), [tennis_3_players_network.dat](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/resources/tennis_data/tennis_3_players_network.dat)):

	//Prepare training set
	val variableIds = Array(
	player1Time0Var.id, player1Time1Var.id, player1Time2Var.id,
	player2Time0Var.id, player2Time1Var.id, player2Time2Var.id,
	player3Time1Var.id, player3Time2Var.id,
	match1v2Time0Var.id, match1v2Time1Var.id, match2v3Time1Var.id, match1v2Time2Var.id, match1v3Time2Var.id, match2v3Time2Var.id)
	
	val dataSet = DataSet.fromFile("src/test/resources/tennis_data/tennis_3_players_network.dat", variableIds)
	
	//Learn parameters
	val maxIterNum = 5
	GenericEMLearn.learn(tennisClusterGraph, dataSet, maxIterNum)
	
	//Prior parameter - Array(0.4729, 0.2323, 0.2947)
	tennisClusterGraph.getCluster(player1Time0Var.id).getFactor()
	
	//Transition parameter - Array(0.9998, 0.0001, 0.0001, 0.0083, 0.9720, 0.0197, 0.0020, 0.0091, 0.9890)
	tennisClusterGraph.getCluster(player2Time1Var.id).getFactor()
	
	//Emission parameter - Array(0.0000, 1.0000, 0.0000, 1.0000, 0.0000, 1.0000, 0.9930, 0.0070, 0.9198, 0.0802, 0.8337, 0.1663, 0.9980, 0.0020, 0.9956, 0.0044, 0.9960, 0.0040)
	tennisClusterGraph.getCluster(match1v2Time2Var.id).getFactor()

References
---------------
1. Daphne Koller, Nir Friedman. Probabilistic Graphical Models, Principles and Techniques, 2009
2. Automated Reasoning Group of Professor Adnan Darwiche at UCLA. SamIam: Sensitivity Analysis, Modelling, Inference and More, version 3.0
3. Adnan Darwiche. Modeling and Reasoning with Bayesian Networks, 2009
4. Stuart Russell, Peter Norvig. Artificial Intelligence - A Modern Approach, Third Edition, 2010
5. Kevin P. Murphy. A Variational Approximation for Bayesian Networks with Discrete and Continuous Latent Variables, 1999
6. Christopher M. Bishop. Pattern Recognition and Machine Learning (Information Science and Statistics), 2009
7. Carl Edward Rasmussen and Christopher K. I. Williams, The MIT Press, 2006