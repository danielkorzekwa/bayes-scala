package dk.bayes.infer.gp.hgp

import org.junit._
import Assert._
import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import java.io.File
import dk.gp.cov.CovSEiso
import breeze.linalg._
import breeze.numerics._

class hgpPredictTest {

  //[x,y]
  val data = csvread(new File("src/test/resources/gpml/regression_data.csv"), skipLines = 1)
  val x = data(::, 0 to 0)
  val y = data(::, 1)

  val x1 = DenseMatrix.horzcat(DenseMatrix.zeros[Double](y.size, 1) + 1.0, x)
  val x2 = DenseMatrix.horzcat(DenseMatrix.zeros[Double](y.size, 1) + 2.0, x)
  val x3 = DenseMatrix.horzcat(DenseMatrix.zeros[Double](1, 1) + 3.0, x(0 to 0, ::))

  val allX = DenseMatrix.vertcat(x1, x2, x3)
  val allY = DenseVector.vertcat(y, y, y(0 to 0))
  val u = DenseMatrix.horzcat(DenseMatrix.zeros[Double](y.size, 1) - 1.0, x)

  val covFunc = CovSEiso()
  val covFuncParams = DenseVector(0.68594, -0.99340)
  val likNoiseLogStdDev = -1.9025

  @Ignore @Test def test = {

    val hgpModel = HgpModel(allX, allY, u, covFunc, covFuncParams, likNoiseLogStdDev)

    val xTest = DenseMatrix((1.0, -1.0), (2.0, 1.0), (3.0, 1.0), (99.0, 1.0))
    val predicted = hgpPredict(xTest, hgpModel)

    println(predicted)
  }

}