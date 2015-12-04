# Clutter problem

The clutter problem is concerned with estimating the state of gaussian variable from noisy obserations which are embedded in background clutter. This problem is used by Tom Minka in his [PhD thesis] to illustrate the Expectation Propagation algorithm.

![Clutter problem](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/dsl/clutter_problem/clutter_problem.png "Clutter problem")

Compute posterior of *x* given two noisy obserations
([source code](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/dsl/demo/ClutterProblemTest.scala)):

```scala
  val x = Gaussian(15, 100)
  val y1 = ClutteredGaussian(x,w = 0.4, a = 10, value = 3)
  val y2 = ClutteredGaussian(x,w = 0.4, a = 10, value = 5)

  infer(x) // Gaussian(4.3431,4.3163)
```

[PhD thesis]: http://research.microsoft.com/en-us/um/people/minka/papers/ep/minka-thesis.pdf