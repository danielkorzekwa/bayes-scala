Bayesian Inference Engine in Scala
===========

Moving the Bayesian Inference Scala code from https://github.com/danielkorzekwa/tennis-rating-dbn-em-scala to this project - in progress...

Functionality:

 * Discrete Factor - product, marginal, evidence, normalise
 * Cluster Loopy BP inference
 * Expectation Maximisation in Dynamic Bayesian Networks

Getting Started - Loopy Belief Propagation in a Cluster Graph [1](#references)
---------------

Consider the following example of a Bayesian Network [1](#references), created with SamIam tool [2](#references).

![Student Bayesian Network](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/student_bn.png "Student Bayesian Network")

### Example 1: Create cluster graph and compute beliefs for Grade variable

Create cluster graph:

	//Create variables
	val difficultyVar = Var(1, 2)
	val intelliVar = Var(2, 2)
	val gradeVar = Var(3, 3)
	val satVar = Var(4, 2)
	val letterVar = Var(5, 2)
	
	//Create factors
	val difficultyFactor = GenericFactor(difficultyVar, Array(0.6, 0.4))
	val intelliFactor = GenericFactor(intelliVar, Array(0.7, 0.3))
	val gradeFactor = GenericFactor(intelliVar, difficultyVar, gradeVar, Array(0.3, 0.4, 0.3, 0.05, 0.25, 0.7, 0.9, 0.08, 0.02, 0.5,0.3,0.2))
	val satFactor = GenericFactor(intelliVar, satVar, Array(0.95, 0.05, 0.2, 0.8))
	val letterFactor = GenericFactor(gradeVar, letterVar, Array(0.1, 0.9, 0.4, 0.6, 0.99, 0.01))
	
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
	clusterGraph.calibrate()
	
	//Get beliefs for Grade variable
	val gradeMarginal = clusterGraph.marginal(gradeVar.id)
	gradeMarginal.getVariables() // Var(3,3)
    gradeMarginal.getValues() // List(0.3620, 0.2884, 0.3496)
	
### Example 2: Compute beliefs for Grade variable given SAT test is high

Set evidence for SAT variable and get Grade marginal:

	clusterGraph.setEvidence(satVar.id, 0)
	clusterGraph.calibrate()
	
	val gradeMarginal = clusterGraph.marginal(gradeVar.id)
	gradeMarginal.getVariables() // Var(3,3)
	gradeMarginal.getValues() // List(0.2446, 0.3257, 0.4295)

References
---------------
1. Daphne Koller, Nir Friedman. Probabilistic Graphical Models, Principles and Techniques, 2009
2. Automated Reasoning Group of Professor Adnan Darwiche at UCLA. SamIam: Sensitivity Analysis, Modelling, Inference and More, version 3.0