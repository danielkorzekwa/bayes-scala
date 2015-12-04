# Gaussian process regression with cluttered Gaussian likelihood

[Gaussian Processes book] by Carl Edward Rasmussen and Christopher K. I. Williams, The MIT Press, 2006. 

![Gaussian process regression with cluttered Gaussian likelihood](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/dsl/gaussian_process_regression_cluttered_gaussian_likelihood/gaussian_process_regression_cluttered_gaussian_likelihood.png "Gaussian process regression with cluttered Gaussian likelihood")

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

[Gaussian Processes book]: http://www.gaussianprocess.org/gpml/