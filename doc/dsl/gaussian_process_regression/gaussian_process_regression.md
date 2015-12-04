# Gaussian process regression

[Gaussian Processes book] by Carl Edward Rasmussen and Christopher K. I. Williams, The MIT Press, 2006. 

![Gaussian process regression](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/dsl/gaussian_process_regression/gaussian_process_regression.png "Gaussian process regression")

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

[Gaussian Processes book]: http://www.gaussianprocess.org/gpml/