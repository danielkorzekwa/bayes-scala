package dk.bayes.model.factor

import scala.Vector

import org.junit.Assert._
import org.junit.Test

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import dk.bayes.math.linear.isIdentical

class LinearGaussianFactorTest {

  val linearGaussianFactor = LinearGaussianFactor(parentVarId = 10, varId = 20, a = 2, b = 0, v = 0.3)

  @Test def getVariablesIds = {
    assertEquals(List(10, 20), linearGaussianFactor.getVariableIds())
  }

  @Test(expected = classOf[IllegalArgumentException]) def product_with_table_factor:Unit = {
    val tableFactor = SingleTableFactor(1, 2, Array(0.6, 0.4))
    linearGaussianFactor * tableFactor
  }

  @Test(expected = classOf[IllegalArgumentException]) def product_incorrect_var_id: Unit = {
    val parentGaussianFactor = GaussianFactor(123, 0, 1)

    linearGaussianFactor * parentGaussianFactor
  }

  @Test def product_with_parent_gaussian = {
    val parentGaussianFactor = GaussianFactor(10, 8, 0.1)

    val productFactor = linearGaussianFactor * parentGaussianFactor

    assertEquals(Vector(10, 20), productFactor.getVariableIds())
    assertTrue(isIdentical(DenseVector(8d, 16d), productFactor.mean, 0.0001))
    assertTrue(isIdentical(new DenseMatrix(2, 2, Array(0.1, 0.2, 0.2, 0.7)), productFactor.variance, 0.0001))
  }

  @Test def product_with_parent_gaussian2 = {
    val linearGaussianFactor = LinearGaussianFactor(parentVarId = 10, varId = 20, a = -0.1d, b = 2, v = 0.5)
    val parentGaussianFactor = GaussianFactor(10, 3, 1.5)

    val productFactor = linearGaussianFactor * parentGaussianFactor

    assertEquals(Vector(10, 20), productFactor.getVariableIds())
    assertTrue(isIdentical(DenseVector(3d, 1.7d), productFactor.mean, 0.0001))
    assertTrue(isIdentical(new DenseMatrix(2, 2, Array(1.5, -0.15, -0.15, 0.515)), productFactor.variance, 0.0001))
  }

  @Test def product_with_child_gaussian = {
    val childGaussianFactor = GaussianFactor(20, 8, 0.1)

    val productFactor = linearGaussianFactor * childGaussianFactor

    assertEquals(Vector(10, 20), productFactor.getVariableIds())
    assertTrue(isIdentical(DenseVector(4d, 8), productFactor.mean, 0.0001))
    assertTrue(isIdentical(new DenseMatrix(2, 2, Array(0.1, 0.05, 0.05, 0.1)), productFactor.variance, 0.0001))
  }

  @Test def product_with_child_gaussian2 = {
    val linearGaussianFactor = LinearGaussianFactor(parentVarId = 10, varId = 20, a = -0.1d, b = 2, v = 0.5)
    val childGaussianFactor = GaussianFactor(20, 5, 2.5)

    val productFactor = linearGaussianFactor * childGaussianFactor

    assertEquals(Vector(10, 20), productFactor.getVariableIds())
    assertTrue(isIdentical(DenseVector(-30d, 5), productFactor.mean, 0.0001))
    assertTrue(isIdentical(new DenseMatrix(2, 2, Array(300d, -25, -25, 2.5d)), productFactor.variance, 0.0001))
  }

}