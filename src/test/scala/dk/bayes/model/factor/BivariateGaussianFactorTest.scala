package dk.bayes.model.factor

import scala.Vector

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import dk.bayes.math.linear.isIdentical

class BivariateGaussianFactorTest {

  private val bivariateGaussianFactor = BivariateGaussianFactor(10, 20, DenseVector(3, 1.7), new DenseMatrix(2, 2, Array(1.5, -0.15, -0.15, 0.515)))

  @Test def getVariablesIds {
    assertEquals(List(10, 20), bivariateGaussianFactor.getVariableIds())
  }

  @Test(expected = classOf[IllegalArgumentException]) def product_with_table_factor {
    val tableFactor = SingleTableFactor(1, 2, Array(0.6, 0.4))
    bivariateGaussianFactor * tableFactor
  }

  @Test(expected = classOf[IllegalArgumentException]) def product_incorrect_var_id {
    val gaussianFactor = GaussianFactor(123, 0, 1)
    bivariateGaussianFactor * gaussianFactor
  }

  @Test def product_with_parent_gaussian {
    val parentGaussianFactor = GaussianFactor(10, 8, 0.1)

    val productFactor = bivariateGaussianFactor * parentGaussianFactor

    assertEquals(Vector(10, 20), productFactor.getVariableIds())
     assertTrue(isIdentical(DenseVector(7.6875d, 1.231d), productFactor.mean,0.001))
     assertTrue(isIdentical(new DenseMatrix(2, 2, Array(0.094, -0.009, -0.009, 0.501)), productFactor.variance,0.001))
  }

  @Test def product_with_child_gaussian {
    val childGaussianFactor = GaussianFactor(20, 8, 0.1)

    val productFactor = bivariateGaussianFactor * childGaussianFactor

    assertEquals(Vector(10, 20), productFactor.getVariableIds())
     assertTrue(isIdentical(DenseVector(1.463d, 6.976), productFactor.mean,0.001))
     assertTrue(isIdentical(new DenseMatrix(2, 2, Array(1.463, -0.024, -0.024, 0.084)), productFactor.variance,0.001))
  }

}