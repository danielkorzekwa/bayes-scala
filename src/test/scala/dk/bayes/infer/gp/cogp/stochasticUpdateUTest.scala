package dk.bayes.infer.gp.cogp

import org.junit._
import Assert._
import breeze.linalg.DenseVector
import breeze.linalg.DenseMatrix
import dk.bayes.infer.gp.cov.CovSEiso
import scala.math._
import scala.language.implicitConversions
import dk.bayes.math.linear.Matrix

class stochasticUpdateUTest {

  val x: Array[Double] = (-10d to 10 by 1d).toArray
  val y = DenseMatrix.zeros[Double](x.size, 2) + 1d

  val covFunc = CovSEiso(log(1), log(1))

  val uMean = DenseVector.zeros[Double](x.size)
  val kXZ: DenseMatrix[Double] = covFunc.cov(x)
  val kZZ = kXZ

  val j = 0

  //likelihood noise precision
  val beta = DenseVector(1d / 0.01, 1d / 0.02) // [P x 1]

  //mixing weights
  val w = new DenseMatrix(2, 1, Array(1.1, -1.2)) // [P x Q]

  val mQ = Array.fill(1)(DenseVector.zeros[Double](x.size))
  val mP = Array.fill(2)(DenseVector.zeros[Double](x.size))

  val l = 0.1

  @Test def test {

    var currmQ = mQ
    var currS = kZZ

    (1 to 100).foreach { iter =>

      val (newUMean, newUVariance) = stochasticUpdateU(j, beta, w, currmQ, mP, currS, kZZ, kXZ, y, l)
      println("uMean=" + newUMean)
      currmQ = Array(newUMean)
      currS = newUVariance
    }

  }

  private implicit def toDenseMatrix(m: Matrix): DenseMatrix[Double] = {
    DenseMatrix(m.t.toArray).reshape(m.numRows, m.numCols)
  }
}