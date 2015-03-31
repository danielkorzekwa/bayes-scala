package dk.bayes.dsl.demo.conversionrate

import org.junit._
import Assert._
import scala.math._
import dk.bayes.math.linear.Matrix
import scala.util.Random
import dk.bayes.math.gaussian.Gaussian
import dk.bayes.dsl.variable.categorical.MvnGaussianThreshold
import dk.bayes.dsl.variable.categorical.MvnGaussianThreshold
import dk.bayes.dsl.variable.categorical.MvnGaussianThreshold
import dk.bayes.dsl.variable.gaussian.multivariate.MultivariateGaussian
import dk.bayes.dsl._

/**
 * For practical usage, don't create single variable for every click. Instead, use one variable for all item clicks.
 */
class ConversionRateTest {

  val conversionRates = List(
    ConversionRate(Item("Butterfly", "Grubba"), 1, 1),
    ConversionRate(Item("Butterfly", "Maze"), 10, 4),
    ConversionRate(Item("Tibhar", "Samsonov"), 10, 1),
    ConversionRate(Item("Tibhar", "Stratus"), 1, 1))

  val items = conversionRates.map(_.item)
  
  @Test def test = {

    /**
     * Build Gaussian Process model
     */

    val itemPopularityMean = Matrix.zeros(items.size, 1)

    val itemPopularityCovFunc = ItemPopularityCovFunc(
      brandLogSf = log(1), brandLogEll = log(1), modelLogSf = log(0.5), modelLogEll = log(1))

    val itemPopularityCov = itemPopularityCovFunc.covarianceMatrix(items)
    val itemPopularitiesVariable = MultivariateGaussian(itemPopularityMean, itemPopularityCov)

    val conversionLikNoise = 1
    val conversionVariables = conversionRates.zipWithIndex.flatMap {
      case (c, index) =>

        val converted = (1 to c.conversions).map { i =>
          MvnGaussianThreshold(itemPopularitiesVariable, index, v = conversionLikNoise, exceeds = Some(true))
        }

        val landedOff = (1 to (c.clicks - c.conversions)).map { i =>
          MvnGaussianThreshold(itemPopularitiesVariable, index, v = conversionLikNoise, exceeds = Some(false))
        }

        List(converted, landedOff)
    }

    /**
     * Predict item similarities and conversion probabilities
     */

    //Predict item popularities
    val itemPopularitiesMarginal = infer(itemPopularitiesVariable)

    assertEquals(0.519, infer(MvnGaussianThreshold(itemPopularitiesMarginal.copy(), 0,v = conversionLikNoise)).m, 0.001)
    assertEquals(0.0347, infer(MvnGaussianThreshold(itemPopularitiesMarginal.copy(), 0,v = conversionLikNoise)).v, 0.001)

    //Predict conversion probability for Butterfly/Maze item
    assertEquals(0.438, infer(MvnGaussianThreshold(itemPopularitiesMarginal.copy(), 1,v = conversionLikNoise)).m, 0.001)
    assertEquals(0.018, infer(MvnGaussianThreshold(itemPopularitiesMarginal.copy(), 1,v = conversionLikNoise)).v, 0.001)

    assertEquals(0.2067, infer(MvnGaussianThreshold(itemPopularitiesMarginal.copy(), 2,v = conversionLikNoise)).m, 0.001)
    assertEquals(0.0158, infer(MvnGaussianThreshold(itemPopularitiesMarginal.copy(), 2,v = conversionLikNoise)).v, 0.001)

    assertEquals(0.354, infer(MvnGaussianThreshold(itemPopularitiesMarginal.copy(), 3,v = conversionLikNoise)).m, 0.001)
    assertEquals(0.0381, infer(MvnGaussianThreshold(itemPopularitiesMarginal.copy(), 3,v = conversionLikNoise)).v, 0.001)

  }
}