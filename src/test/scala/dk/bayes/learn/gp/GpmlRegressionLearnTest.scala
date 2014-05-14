package dk.bayes.learn.gp

import org.junit._
import Assert._
import breeze.linalg.DenseVector
import breeze.optimize.DiffFunction
import breeze.linalg._
import breeze.optimize.LBFGS
import dk.bayes.math.linear._
import scala.math._

/**
 * Learning gpml following http://www.gaussianprocess.org/gpml/code/matlab/doc/index.html regression example
 */
class GpmlRegressionLearnTest {

  //[x,y]
  private val data = loadCSV("src/test/resources/gpml/regression_data.csv", 1)
  private val x = data.column(0)
  private val y = data.column(1)

  @Test def test {

    // logarithm of [signal standard deviation,length-scale,likelihood noise standard deviation] 
    val initialParams = DenseVector(log(1d), log(1), log(0.1))
    val diffFunction = GpDiffFunction(x, y)

    val optimizer = new LBFGS[DenseVector[Double]](maxIter = 100, m = 3, tolerance = 1.0E-6)
    val newParams = optimizer.minimize(diffFunction, initialParams)

    assertEquals(0.68594, newParams(0), 0.0001)
    assertEquals(-0.99340, newParams(1), 0.0001)
    assertEquals(-1.9025, newParams(2), 0.0001)
    println("Learned gp parameters:" + newParams)
  }

}