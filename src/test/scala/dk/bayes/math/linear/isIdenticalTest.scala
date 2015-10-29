package dk.bayes.math.linear

import org.junit._
import org.junit.Assert._
import breeze.linalg.DenseVector
import breeze.linalg.DenseMatrix

object isIdenticalTest {

  @Test def test = {

    assertEquals(false, isIdentical(DenseMatrix((-1.1, 2.0), (3.0, 4.0)), DenseMatrix((-1.1, 2.0), (3.1, 4.0)), 0.0))
    assertEquals(true, isIdentical(DenseMatrix((-1.1, 2.0), (3.0, 4.0)), DenseMatrix((-1.1, 2.0), (3.0, 4.0)), 0.0))
    assertEquals(true, isIdentical(DenseMatrix((-1.1, 2.0), (3.0, 4.0)), DenseMatrix((-1.1, 2.0), (3.1, 4.0)), 0.2))
    assertEquals(true, isIdentical(DenseMatrix((-1.1, 2.0), (3.0, 4.0)), DenseMatrix((-1.1, 2.0), (2.9, 4.0)), 0.2))

    assertEquals(false, isIdentical(DenseVector(-1.1, 2), DenseVector(-1.1, 2.1), 0.0))
    assertEquals(true, isIdentical(DenseVector(-1.1, 2), DenseVector(-1.1, 2), 0.0))
    assertEquals(true, isIdentical(DenseVector(-1.1, 2), DenseVector(-1.1, 2.1), 0.2))
    assertEquals(true, isIdentical(DenseVector(-1.1, 2), DenseVector(-1.1, 1.9), 0.2))

  }

}