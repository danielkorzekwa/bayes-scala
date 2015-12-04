# TrueSkill

[TrueSkill] is a skill based probabilistic ranking system developed by Thore Graepel and Tom Minka at Microsoft Research Centre in Cambridge.

![TrueSkill two players network](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/dsl/true_skill/tennis_trueskill_bn.png "TrueSkill two players network")

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

[TrueSkill]: http://research.microsoft.com/en-us/projects/trueskill/