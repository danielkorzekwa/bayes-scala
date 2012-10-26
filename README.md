Bayesian Inference Engine in Scala
===========

Moving the Bayesian Inference Scala code from https://github.com/danielkorzekwa/tennis-rating-dbn-em-scala to this project - in progress...

Functionality:

 * Discrete Factor - product, marginal, evidence, normalise
 * Cluster Loopy BP inference
 * Expectation Maximisation in Dynamic Bayesian Networks

Getting Started - Loopy Belief Propagation in a Cluster Graph [1](#references)
---------------

Consider the following example of a Bayesian Network [1](#references).

![Student Bayesian Network](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/student_bn.png "Student Bayesian Network")

### Example 1: Compute beliefs for Grade variable

	//Create variables
	val difficultyVar = Var(1, 2)
	val intelliVar = Var(2, 2)
	val gradeVar = Var(3, 3)
	val satVar = Var(4, 2)
	val letterVar = Var(5, 2)
	
	//Create factors
	val difficultyFactor = GenericFactor(difficultyVar, Array(0.6, 0.4))
	val intelliFactor = GenericFactor(intelliVar, Array(0.7, 0.3))
	val gradeFactor = GenericFactor(intelliVar, difficultyVar, gradeVar, Array(0.3, 0.4, 0.3, 0.005, 0.25, 0.7, 0.9, 0.3, 0.2))
	val satFactor = GenericFactor(intelliVar, satVar, Array(0.95, 0.05, 0.2, 0.8))
	val letterFactor = GenericFactor(gradeVar, letterVar, Array(0.1, 0.9, 0.4, 0.6, 0.99, 0.01))
	
	//Create cluster graph
	val clusterGraph = GenericClusterGraph()
	clusterGraph.addCluster(1, difficultyVar)
	clusterGraph.addCluster(2, intelliVar)
	clusterGraph.addCluster(3, gradeVar)
	clusterGraph.addCluster(4, satVar)
	clusterGraph.addCluster(5, letterVar)
	
	//Add edges between clusters in a cluster graph
	clusterGraph.addEdges((1, 3), (2, 3), (2, 4), (3, 5))
	
	//Calibrate cluster graph
	clusterGraph.calibrate()
	
	//Compute beliefs for Grade variable
	val gradeMarginal = clusterGraph.marginal(gradeVar.id)
	println(gradeMarginal.getVariables()) 
	println(gradeMarginal.getValues())
 
### Example 2: Compute beliefs for Grade variable given Intelligence is high
 
@TODO
 
References
---------------
1.  Daphne Koller, Nir Friedman. Probabilistic Graphical Models, Principles and Techniques, 2009