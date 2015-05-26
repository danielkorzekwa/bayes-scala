package dk.bayes.infer.gp.sgpr

import org.junit._
import Assert._
import dk.bayes.infer.gp.cov.CovSEiso
import dk.bayes.math.linear.Matrix
import dk.bayes.infer.gp.gpr.GenericGPRegression
import dk.bayes.math.linear._
import dk.bayes.math.gaussian.MultivariateGaussian
import scala.math._
import breeze.linalg.DenseVector
import breeze.optimize.LBFGS

class GenericSparseGPRLearnTest {

  //Sample data
  //     val covFunc = CovSEiso(sf = 0.68594, ell = -0.99340)
  //      val noiseLogStdDev = -1.9025
  //    
  //       val x = Matrix((-20d to 20 by 0.01).toArray)
  //       println(x.numRows())
  //       val variance = covFunc.cov(x) + Matrix.identity(x.numRows())*pow(exp(noiseLogStdDev),2)
  //       val mvn = MultivariateGaussian(x,variance)
  //       val y = Matrix(mvn.draw(5656))
  //       x.toArray.zip(y.toArray).foreach(x => println(x._1 + "," + x._2))

  @Test def learn_pseudo_inducing_points_not_large_scale = {

    val data = loadCSV("src/test/resources/gpml/regression_data.csv", 1)
    val x = data.column(0)
    val y = data.column(1)

    val u = Matrix((-2d to 2 by 0.05).toArray)

    // logarithm of [signal standard deviation,length-scale,likelihood noise standard deviation] 
    val initialParams = DenseVector(log(1d), log(1), log(0.1))
    val diffFunction = SparseGpDiffFunction(x, y, u)

    val optimizer = new LBFGS[DenseVector[Double]](maxIter = 100, m = 3, tolerance = 1.0E-4)
    val optIterations = optimizer.iterations(diffFunction, initialParams).toList

    println("Initial loglik" + diffFunction.calculate(initialParams)._1)
    println("Final loglik" + diffFunction.calculate(optIterations.last.x)._1)
    assertEquals(16, optIterations.size)

    val newParams = optIterations.last.x
    //assert -negative log likelihood
    assertEquals(154.0786, diffFunction.calculate(initialParams)._1, 0.0001)
    assertEquals(14.1135, diffFunction.calculate(newParams)._1, 0.0001)

    assertEquals(0.6883, newParams(0), 0.0001)
    assertEquals(-0.9970, newParams(1), 0.0001)
    assertEquals(-1.9013, newParams(2), 0.0001)
    println("Learned gp parameters:" + newParams)
  }

  @Test def learn_pseudo_inducing_points_large_scale = {

    val data = loadCSV("src/test/resources/gpml/regression_data_2K.csv", 1)
    val x = data.column(0)
    val y = data.column(1)

    val u = Matrix((-20d to 0 by 1).toArray)

    // logarithm of [signal standard deviation,length-scale,likelihood noise standard deviation] 
    val initialParams = DenseVector(log(1d), log(1), log(0.1))
    val diffFunction = SparseGpDiffFunction(x, y, u)

    val optimizer = new LBFGS[DenseVector[Double]](maxIter = 100, m = 3, tolerance = 1.0E-4)
    val optIterations = optimizer.iterations(diffFunction, initialParams).toList

    println("Initial loglik" + diffFunction.calculate(initialParams)._1)
    println("Final loglik" + diffFunction.calculate(optIterations.last.x)._1)
    assertEquals(15, optIterations.size)

    val newParams = optIterations.last.x
    //assert -negative log likelihood
    assertEquals(68873.7356, diffFunction.calculate(initialParams)._1, 0.0001)
    assertEquals(2649.04376, diffFunction.calculate(newParams)._1, 0.0001)

    assertEquals(1.5335, newParams(0), 0.0001)
    assertEquals(0.1205, newParams(1), 0.0001)
    assertEquals(-0.1431, newParams(2), 0.0001)
    println("Learned gp parameters:" + newParams)
  }
}