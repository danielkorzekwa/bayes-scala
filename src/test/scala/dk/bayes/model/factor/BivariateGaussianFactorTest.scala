package dk.bayes.model.factor

import org.junit._
import Assert._
import dk.bayes.math.linear._

class BivariateGaussianFactorTest {

  private val bivariateGaussianFactor = BivariateGaussianFactor(10, 20, Matrix(3, 1.7), Matrix(2, 2, Array(1.5, -0.15, -0.15, 0.515)))

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
    assertEquals(Matrix(7.688d, 1.231d).toString, productFactor.mean.toString)
    assertEquals(Matrix(2, 2, Array(0.094, -0.009, -0.009, 0.501)).toString, productFactor.variance.toString)
  }

  @Test def product_with_child_gaussian {
    val childGaussianFactor = GaussianFactor(20, 8, 0.1)

    val productFactor = bivariateGaussianFactor * childGaussianFactor

    assertEquals(Vector(10, 20), productFactor.getVariableIds())
    assertEquals(Matrix(1.463d, 6.976).toString, productFactor.mean.toString)
    assertEquals(Matrix(2, 2, Array(1.463, -0.024, -0.024, 0.084)).toString, productFactor.variance.toString)
  }

}