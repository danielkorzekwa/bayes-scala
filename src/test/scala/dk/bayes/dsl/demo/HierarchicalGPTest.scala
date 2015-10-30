package dk.bayes.dsl.demo

import org.junit.Assert.assertTrue
import org.junit.Test

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import breeze.linalg.cholesky
import breeze.numerics.log
import dk.bayes.dsl.infer
import dk.bayes.dsl.variable.Gaussian
import dk.bayes.infer.gp.cov.CovSEiso
import dk.bayes.math.linear.invchol
import dk.bayes.math.linear.isIdentical

class HierarchicalGPTest {

  val u = DenseMatrix(0.0, 1)

  val x1 = u
  val y1 = DenseVector(5.0, 8)

  val x2 = u
  val y2 = DenseVector(6.0, 4.0)

  val covFunc = new CovSEiso(log(1), log(1))
  val kUU = covFunc.cov(u) + DenseMatrix.eye[Double](u.rows) * 1e-7
  val kXU1 = covFunc.covNM(x1, u)
  val kXU2 = covFunc.covNM(x2, u)

  @Test def test_x1 = {

    val uVariable = Gaussian(DenseVector.zeros[Double](u.rows), kUU)

    val x1A = kXU1 * invchol(cholesky(kUU).t)
    val x1b = DenseVector.zeros[Double](x1.rows)
    val x1v = x1A * kXU1.t + DenseMatrix.eye[Double](x1.rows) * 1d
    val x1Variable = Gaussian(x1A, uVariable, x1b, x1v, yValue = y1)

    val uPosterior = infer(uVariable)
    assertTrue("actual=" + uPosterior.m, isIdentical(DenseVector(2.148, 2.8088), uPosterior.m, 0.001))

  }
}