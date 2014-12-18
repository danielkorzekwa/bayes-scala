package dk.bayes.model.factor

import org.junit.Assert.assertEquals
import org.junit.Test
import dk.bayes.math.linear._

class LinearGaussianFactorTest {

  val linearGaussianFactor = LinearGaussianFactor(parentVarId = 10, varId = 20, a = 2, b = 0, v = 0.3)

  @Test def getVariablesIds {
    assertEquals(List(10, 20), linearGaussianFactor.getVariableIds())
  }

  @Test(expected = classOf[IllegalArgumentException]) def product_with_table_factor {
    val tableFactor = SingleTableFactor(1, 2, Array(0.6, 0.4))
    linearGaussianFactor * tableFactor
  }

  @Test(expected = classOf[IllegalArgumentException]) def product_incorrect_var_id {
    val parentGaussianFactor = GaussianFactor(123, 0, 1)

    linearGaussianFactor * parentGaussianFactor
  }

  @Test def product_with_parent_gaussian {
    val parentGaussianFactor = GaussianFactor(10, 8, 0.1)

    val productFactor = linearGaussianFactor * parentGaussianFactor

    assertEquals(Vector(10, 20), productFactor.getVariableIds())
    assertEquals(Matrix(8d, 16d).toString, productFactor.mean.toString)
    assertEquals(Matrix(2, 2, Array(0.1, 0.2, 0.2, 0.7)).toString(), productFactor.variance.toString)
  }

  @Test def product_with_parent_gaussian2 {
    val linearGaussianFactor = LinearGaussianFactor(parentVarId = 10, varId = 20, a = -0.1d, b = 2, v = 0.5)
    val parentGaussianFactor = GaussianFactor(10, 3, 1.5)

    val productFactor = linearGaussianFactor * parentGaussianFactor

    assertEquals(Vector(10, 20), productFactor.getVariableIds())
    assertEquals(Matrix(3d, 1.7d).toString, productFactor.mean.toString)
    assertEquals(Matrix(2, 2, Array(1.5, -0.15, -0.15, 0.515)).toString, productFactor.variance.toString)
  }

  @Test def product_with_child_gaussian {
    val childGaussianFactor = GaussianFactor(20, 8, 0.1)

    val productFactor = linearGaussianFactor * childGaussianFactor

    assertEquals(Vector(10, 20), productFactor.getVariableIds())
    assertEquals(Matrix(4d, 8).toString, productFactor.mean.toString)
    assertEquals(Matrix(2, 2, Array(0.1, 0.05, 0.05, 0.1)).toString, productFactor.variance.toString)
  }

  @Test def product_with_child_gaussian2 {
    val linearGaussianFactor = LinearGaussianFactor(parentVarId = 10, varId = 20, a = -0.1d, b = 2, v = 0.5)
    val childGaussianFactor = GaussianFactor(20, 5, 2.5)

    val productFactor = linearGaussianFactor * childGaussianFactor

    assertEquals(Vector(10, 20), productFactor.getVariableIds())
    assertEquals(Matrix(-30d, 5).toString, productFactor.mean.toString)
    assertEquals(Matrix(2, 2, Array(300d, -25, -25, 2.5d)).toString, productFactor.variance.toString)
  }

}