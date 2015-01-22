# Bayesian Networks in Scala [![Build Status](https://travis-ci.org/danielkorzekwa/bayes-scala.svg)](https://travis-ci.org/danielkorzekwa/bayes-scala)

It is a Scala library for building Bayesian Networks with discrete/continuous variables and running deterministic Bayesian inference.

* Examples illustrating the usage of a high level API for building Bayesian Networks
  * [Student Bayesian Network](#student-bayesian-network) 
  * [Monty Hall problem](#monty-hall-problem)
  * [TrueSkill](#trueskill)
  * [Clutter problem](#clutter-problem) 
  * [Gaussian process regression](#gaussian-process-regression)
  * [Gaussian process regression with cluttered Gaussian likelihood](#gaussian-process-regression-with-cluttered-gaussian-likelihood)
  * [1D Kalman filter](#1d-kalman-filter)

* [Low level algorithms] which are used behind the scenes for Bayesian Inference, e.g. Loopy Belief Propagation, Expectation Propagation

## Examples

### Student Bayesian Network

Bayesian Networks diagram was created with the [SamIam] tool. 
Student model was borrowed from the book of *Daphne Koller, Nir Friedman. Probabilistic Graphical Models, Principles and Techniques, 2009, page 53*   

![Student Bayesian Network](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/student_bn.png "Student Bayesian Network")

Infer marginal for *grade* variable
([source code](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/dsl/demo/StudentTest.scala)):

```scala
  val difficulty = Categorical(Vector(0.6, 0.4))
  val intelli = Categorical(Vector(0.7, 0.3))
  val grade = Categorical(intelli, difficulty, Vector(0.3, 0.4, 0.3, 0.05, 0.25, 0.7, 0.9, 0.08, 0.02, 0.5, 0.3, 0.2))
  val sat = Categorical(intelli, Vector(0.95, 0.05, 0.2, 0.8))
  val letter = Categorical(grade, Vector(0.1, 0.9, 0.4, 0.6, 0.99, 0.01))

  infer(grade) // List(0.3620, 0.2884, 0.3496)
```

Infer posterior for *grade* variable given *sat* is high
([source code](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/dsl/demo/KalmanFilterTest.scala)):


```scala
  sat.setValue(0)

  infer(grade) // List(0.2446, 0.3257, 0.4295)
```

### Monty Hall problem

[Monty Hall problem] on wikipedia.
Bayesian Networks diagram was created with the [SamIam] tool. 

![Student Bayesian Network](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/monty_hall_bn.png "Student Bayesian Network")

Compute the probabilities of winning a car given a guest chooses door 1 and Monty opens door 2
([source code](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/dsl/demo/MontyHallProblemTest.scala)):

```scala

  val carDoor = Categorical(Vector(1d / 3, 1d / 3, 1d / 3))
  val guestDoor = Categorical(Vector(1d / 3, 1d / 3, 1d / 3))

  val montyDoor = Categorical(carDoor, guestDoor, Vector(
    0, 0.5, 0.5,
    0, 0, 1,
    0, 1, 0,
    0, 0, 1,
    0.5, 0, 0.5,
    1, 0, 0,
    0, 1, 0,
    1, 0, 0,
    0.5, 0.5, 0))
    
    guestDoor.setValue(0) //Guest chooses door 0
    montyDoor.setValue(1) //Monty opens door 1 
    
    infer(carDoor) //List(1/3,0,2/3)

```

### TrueSkill

[TrueSkill] is a skill based probabilistic ranking system developed by Thore Graepel and Tom Minka at Microsoft Research Centre in Cambridge.

![TrueSkill two players network](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/trueskill_in_tennis_factor_graph/tennis_trueskill_bn.png "TrueSkill two players network")

Infer posterior for skill given player 1 is a winner
([source code](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/dsl/demo/TrueSkillTwoPlayersTest.scala)):

```scala
  val skill1 = Gaussian(4, 81)
  val skill2 = Gaussian(41, 25)

  val perf1 = Gaussian(skill1, pow(25d / 6, 2))
  val perf2 = Gaussian(skill2, pow(25d / 6, 2))

  val perfDiff = Gaussian(A = Matrix(1.0, -1), Vector(perf1, perf2), v = 0.0)
  val outcomeFactor = Categorical(perfDiff, cdfThreshold = 0, value = 0) //player 1 is a winner

  infer(skill1) // Gaussian(27.1744,37.4973)
```

### Clutter problem

The clutter problem is concerned with estimating the state of gaussian variable from noisy obserations which are embedded in background clutter. This problem is used by Tom Minka in his [PhD thesis] to illustrate the Expectation Propagation algorithm.

![Clutter problem](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/dsl_examples/clutter_problem.png "Clutter problem")

Compute posterior of *x* given two noisy obserations
([source code](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/dsl/demo/ClutterProblemTest.scala)):

```scala
  val x = Gaussian(15, 100)
  val y1 = ClutteredGaussian(x,w = 0.4, a = 10, value = 3)
  val y2 = ClutteredGaussian(x,w = 0.4, a = 10, value = 5)

  infer(x) // Gaussian(4.3431,4.3163)
```

### Gaussian process regression

[Gaussian Processes book] by Carl Edward Rasmussen and Christopher K. I. Williams, The MIT Press, 2006. 

![Gaussian process regression](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/dsl_examples/gaussian_process_regression.png "Gaussian process regression")

Infer posterior of the latent variable *f* given observed values of *y*
([source code](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/dsl/demo/GaussianProcessRegressionTest.scala)):

```scala
  val fMean = Matrix(0, 0, 0)
  val x = Matrix(1, 2, 3)
  val covFunc = CovSEiso(sf = log(7.5120), ell = log(2.1887))
  val fVar = covFunc.cov(x)
  val f = Gaussian(fMean, fVar) //f variable

  val yVar = pow(0.81075, 2) * Matrix.identity(3)
  val y = Gaussian(f, yVar, yValue = Matrix(1.0, 4, 9)) //y variable

  val fPosterior = infer(f) // mean = (0.878, 4.407, 8.614)
```

### Gaussian process regression with cluttered Gaussian likelihood

![Gaussian process regression with cluttered Gaussian likelihood](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/dsl_examples/gaussian_process_regression_cluttered_gaussian_likelihood.png "Gaussian process regression with cluttered Gaussian likelihood")

Infer posterior of the latent variable *f* given observed values of *y*
([source code](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/dsl/demo/GaussianProcessRegressionClutteredGaussianLikelihoodTest.scala)):

```scala
  val fMean = Matrix(0, 0, 0)
  val x = Matrix(1, 2, 3)
  val covFunc = CovSEiso(sf = log(7.5120), ell = log(2.1887))
  val fVar = covFunc.cov(x)
  val f = Gaussian(fMean, fVar) //f variable

  val y0 = ClutteredGaussian(x = f, xIndex = 0, w = 0.4, a = 10, value = 1)
  val y1 = ClutteredGaussian(x = f, xIndex = 1, w = 0.4, a = 10, value = 4)
  val y2 = ClutteredGaussian(x = f, xIndex = 2, w = 0.4, a = 10, value = 9)

  val fPosterior = infer(f) // mean = (0.972, 4.760, 8.386)
```

### 1D Kalman filter

![Kalman 1d two obserations](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/dsl_examples/kalman_1d_two_obserations.png "Kalman 1d two obserations")

Infer new gaussian state given two noisy observation
([source code](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/dsl/demo/KalmanFilterTwoObservationsTest.scala)):

```scala
  val x = Gaussian(3, 1.5)
  val y1 = Gaussian(x, v = 0.9, yValue = 0.6)
  val y2 = Gaussian(x, v = 0.5, yValue = 0.62)

  infer(x) // Gaussian(1.0341,0.2647)
```

[Low level algorithms]: https://github.com/danielkorzekwa/bayes-scala/blob/master/doc/lowlevel/README.md
[SamIam]: http://reasoning.cs.ucla.edu/samiam/
[TrueSkill]: http://research.microsoft.com/en-us/projects/trueskill/
[PhD thesis]: http://research.microsoft.com/en-us/um/people/minka/papers/ep/minka-thesis.pdf
[Monty Hall problem]: http://en.wikipedia.org/wiki/Monty_Hall_problem
[Gaussian Processes book]: http://www.gaussianprocess.org/gpml/