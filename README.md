# Bayesian Networks in Scala

It is a Scala library for building Bayesian Networks with discrete/continuous variables and running deterministic Bayesian inference.

* Examples illustrating the usage of a high level API for building Bayesian Networks
  * [Student Bayesian Network](#student-bayesian-network) 
  * [TrueSkill](#trueskill)
  * [1D Kalman filter](#1d-kalman-filter)

* [Low level algorithms] which are used under the scenes for Bayesian Inference, e.g. Loopy Belief Propagation, Expectation Propagation

## Examples

### Student Bayesian Network

Bayesian Networks diagram was created with the [SamIam] tool. 
Student model is borrowed from the book of *Daphne Koller, Nir Friedman. Probabilistic Graphical Models, Principles and Techniques, 2009, page 53*   

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

### TrueSkill

[TrueSkill] is a skill based probabilistic ranking system developed by Thore Graepel and Tom Minka at Microsoft Research Centre in Cambridge

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
### 1D Kalman filter

Infer new gaussian state given noisy observation
([source code](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/dsl/demo/KalmanFilterTest)):


```scala
  val x = Gaussian(0.5, 2)
  val y = Gaussian(x, 0.1, value = 0.7)

  infer(x) // Gaussian(0.69047,0.0952380)
```


[Low level algorithms]: https://github.com/danielkorzekwa/bayes-scala/blob/master/doc/lowlevel/README.md
[SamIam]: http://reasoning.cs.ucla.edu/samiam/
[TrueSkill]: http://research.microsoft.com/en-us/projects/trueskill/