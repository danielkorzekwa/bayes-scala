package dk.bayes.infer.gp.sgpr

import org.junit._
import Assert._
import dk.bayes.infer.gp.cov.CovSEiso
import dk.bayes.math.linear.Matrix
import dk.bayes.infer.gp.gpr.GenericGPRegression
import dk.bayes.math.linear._
import dk.bayes.math.gaussian.MultivariateGaussian

class GenericSparseGPRTest {

  @Test def test_all_trainingset_as_inducing_points_not_large_scale = {
    val data = loadCSV("src/test/resources/gpml/regression_data.csv", 1)
    val x = data.column(0)
    val y = data.column(1)

    val covFunc = CovSEiso(sf = 0.68594, ell = -0.99340)
    val noiseLogStdDev = -1.9025

    val z = Matrix(Array(-1d, 1))

    val model = GenericSparseGPR(x, y, x, covFunc, noiseLogStdDev)

    val predictions = model.predict(z)

    assertEquals(0.037, predictions(0, 0), 0.0001) //z(0) mean
    assertEquals(0.01825, predictions(0, 1), 0.0001) //z(0) variance

    assertEquals(0.9785, predictions(1, 0), 0.0001) //z(1) mean
    assertEquals(1.7139, predictions(1, 1), 0.0001) //z(1) variance
  }

  @Test def test_pseudo_inducing_points_not_large_scale = {
    val data = loadCSV("src/test/resources/gpml/regression_data.csv", 1)
    val x = data.column(0)
    val y = data.column(1)

    val u = Matrix((-2d to 2 by 0.02).toArray)
    val covFunc = CovSEiso(sf = 0.68594, ell = -0.99340)
    val noiseLogStdDev = -1.9025

    val z = Matrix(Array(-1d, 1))

    val model = GenericSparseGPR(x, y, u, covFunc, noiseLogStdDev)

    val predictions = model.predict(z)

    assertEquals(0.0371, predictions(0, 0), 0.0001) //z(0) mean
    assertEquals(0.01825, predictions(0, 1), 0.0001) //z(0) variance

    assertEquals(0.9783, predictions(1, 0), 0.0001) //z(1) mean
    assertEquals(1.7139, predictions(1, 1), 0.0001) //z(1) variance
  }

  @Test def test_pseudo_inducing_points_large_scale = {

    val data = loadCSV("src/test/resources/gpml/regression_data_4K.csv", 1)
    val x = data.column(0)
    val y = data.column(1)
    val u = Matrix((-20d to 20 by 0.2).toArray)

    val covFunc = CovSEiso(sf = 0.68594, ell = -0.99340)
    val noiseLogStdDev = -1.9025

    val z = Matrix(Array(-1d, 1))

    val model = GenericSparseGPR(x, y, u, covFunc, noiseLogStdDev)

    val predictions = model.predict(z)

    assertEquals(0.5467, predictions(0, 0), 0.0001) //z(0) mean
    assertEquals(8.3925e-4, predictions(0, 1), 0.0001) //z(0) variance

    assertEquals(2.6868, predictions(1, 0), 0.0001) //z(1) mean
    assertEquals(8.3923e-4, predictions(1, 1), 0.0001) //z(1) variance
  }
}