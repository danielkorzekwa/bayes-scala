# 1D Kalman filter

![Kalman 1d two obserations](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/dsl_examples/1d_kalman_filter/kalman_1d_two_obserations.png "Kalman 1d two obserations")

Infer new gaussian state given two noisy observation
([source code](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/dsl/demo/KalmanFilterTwoObservationsTest.scala)):

```scala
  val x = Gaussian(3, 1.5)
  val y1 = Gaussian(x, v = 0.9, yValue = 0.6)
  val y2 = Gaussian(x, v = 0.5, yValue = 0.62)

  infer(x) // Gaussian(1.0341,0.2647)
```