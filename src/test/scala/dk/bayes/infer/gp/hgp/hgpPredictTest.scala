package dk.bayes.infer.gp.hgp

import org.junit._
import Assert._
import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import java.io.File
import dk.gp.cov.CovSEiso
import breeze.linalg._
import breeze.numerics._
import dk.gp.cov.CovFunc

class hgpPredictTest {

  val (allX, allY, u) = getTrainingData()

  val covFunc = TestCovFunc()
  val covFuncParams = DenseVector(log(1d), log(1))
  val likNoiseLogStdDev = log(0.1)

  @Test def test = {

    val initialHgpModel = HgpModel(allX, allY, u, covFunc, covFuncParams, likNoiseLogStdDev)
    val trainedHgpModel = hgpTrain(initialHgpModel)

    assertEquals(0.8575, trainedHgpModel.covFuncParams(0), 0.0001)
    assertEquals(-0.9461, trainedHgpModel.covFuncParams(1), 0.0001)
    assertEquals(-1.8034, trainedHgpModel.likNoiseLogStdDev, 0.0001)

    val xTest = DenseMatrix((1.0, -1.0), (1.0, 1.0), (2.0, -1.0), (2.0, 1.0), (3.0, -1.0), (3.0, 1.0), (99.0, -1.0), (99.0, 1.0))
    val predicted = hgpPredict(xTest, trainedHgpModel)

    assertEquals(0.0072, predicted(0).m, 0.0001)
    assertEquals(1.4895, predicted(1).m, 0.0001)

    assertEquals(0.0512, predicted(2).m, 0.0001)
    assertEquals(0.9524, predicted(3).m, 0.0001)

    assertEquals(0.04871, predicted(4).m, 0.0001)
    assertEquals(1.0525, predicted(5).m, 0.0001)

    assertEquals(0.0408, predicted(6).m, 0.0001)
    assertEquals(1.0577, predicted(7).m, 0.0001)
  }

  /**
   * Returns tasks: 1,2 and 3
   *
   * @returns (x,y,u)
   */
  private def getTrainingData(): (DenseMatrix[Double], DenseVector[Double], DenseMatrix[Double]) = {
    //[x,y]
    val data = csvread(new File("src/test/resources/gpml/regression_data.csv"), skipLines = 1)
    val x = data(::, 0 to 0)
    val y = data(::, 1)

    val x1Idx = List(0, 2, 4, 6, 8, 10, 12, 14, 16, 18)
    val x2Idx = List(1, 3, 5, 7, 9, 11, 13, 15, 17)
    val x1 = DenseMatrix.horzcat(DenseMatrix.zeros[Double](x1Idx.size, 1) + 1.0, x(x1Idx, ::))
    val x2 = DenseMatrix.horzcat(DenseMatrix.zeros[Double](x2Idx.size, 1) + 2.0, x(x2Idx, ::))
    val x3 = DenseMatrix.horzcat(DenseMatrix.zeros[Double](1, 1) + 3.0, x(19 to 19, ::))

    val allX = DenseMatrix.vertcat(x1, x2, x3)
    val allY = DenseVector.vertcat(y(x1Idx).toDenseVector, y(x2Idx).toDenseVector, y(19 to 19))
    val u = DenseMatrix.horzcat(DenseMatrix.zeros[Double](y.size, 1) - 1.0, x)

    (allX, allY, u)
  }

}