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
import org.junit.Ignore
import breeze.linalg.inv

class HierarchicalGPTest {

  val u = DenseMatrix(0.0, 1)

  val x1 = u
  val y1 = DenseVector(5.0, 8)

  val x2 = u
  val y2 = DenseVector(6.0, 4.0)

  val covFunc = new CovSEiso(log(1), log(1))
  val kUU = covFunc.cov(u) + DenseMatrix.eye[Double](u.rows) * 1e-7

  val kXX1 = covFunc.cov(x1) + DenseMatrix.eye[Double](u.rows) * 1e-7
  val kXU1 = covFunc.covNM(x1, u)

  val kXX2 = covFunc.cov(x2) + DenseMatrix.eye[Double](u.rows) * 1e-7
  val kXU2 = covFunc.covNM(x2, u)

  @Test def test_x1x2 = {

    val uVariable = Gaussian(DenseVector.zeros[Double](u.rows), kUU)

    val x1A = kXU1 * invchol(cholesky(kUU).t)
    val x1b = DenseVector.zeros[Double](x1.rows)
    val x1kXUInvLUU = kXU1 * inv(cholesky(kUU).t)
    val x1v = kXX1 - x1kXUInvLUU * x1kXUInvLUU.t + DenseMatrix.eye[Double](x1.rows) * 1d
    val x1Variable = Gaussian(x1A, uVariable, x1b, x1v, yValue = y1)

    val x2A = kXU2 * invchol(cholesky(kUU).t)
    val x2b = DenseVector.zeros[Double](x2.rows)
    val x2kXUInvLUU = kXU2 * inv(cholesky(kUU).t)
    val x2v = kXX2 - x2kXUInvLUU * x2kXUInvLUU.t + DenseMatrix.eye[Double](x2.rows) * 1d
    val x2Variable = Gaussian(x2A, uVariable, x2b, x2v, yValue = y2)

    val uPosterior = infer(uVariable)
    assertTrue("actual=" + uPosterior.m, isIdentical(DenseVector(4.2751, 4.4952), uPosterior.m, 0.001))

  }

  @Test def test_x1 = {

    val uVariable = Gaussian(DenseVector.zeros[Double](u.rows), kUU)

    val x1A = kXU1 * invchol(cholesky(kUU).t)
    val x1b = DenseVector.zeros[Double](x1.rows)

    val x1kXUInvLUU = kXU1 * inv(cholesky(kUU).t)
    val x1v = kXX1 - x1kXUInvLUU * x1kXUInvLUU.t + DenseMatrix.eye[Double](x1.rows) * 1d
    val x1Variable = Gaussian(x1A, uVariable, x1b, x1v, yValue = y1)

    val uPosterior = infer(uVariable)
    assertTrue("actual=" + uPosterior.m, isIdentical(DenseVector(3.5827, 4.4298), uPosterior.m, 0.001))

  }
}