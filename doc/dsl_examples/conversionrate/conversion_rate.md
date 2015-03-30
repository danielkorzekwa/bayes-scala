# Predicting conversion rate with Gaussian Processes

Consider some Internet shop selling the following table tennis blades.

| Brand       | Model       | Clicks  | Conversions|
|-------------|-------------| --------|------------|
| Butterfly   | Grubba      |1        |1           |
| Butterfly   | Maze        |10       |4           |
| Tibhar      | Samsonov    |10       |1           |
| Tibhar      | Stratus     |1        |1           |

In a pure form, **conversion rate** is defined as the proportion of customers who buy a product given they landed on its web page. For example, it would be 0.4 for *Butterfly/Maze blade*. 

It's not very convincing  that both *Butterfly/Grubba* and *Tibhar/Stratus* blades have the same conversion rate of 1, especially if we assume that conversions rates might be correlated across brands and models. Thinking in a more bayesian way, we might want to use a prior belief over possible conversion rates and then possibly end up with a much more plausible predictions.

Bayesian model for predicting conversion rates:

![Conversion rate gp model](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/dsl_examples/conversionrate/conversion_rate_gp_model.png "Convertion rate gp model")

Model variables
* x (observed) - Item (product) offered to customers, characterised by *brand* and *model* attributes.

* f (latent)- Item popularity. One way to interpret this is that popular items are more likely to be sold.

* y (observed) - True if a customer buys a product, otherwise false.  

There are five hyper parameters for this model that define how different items covary with each other. They might be learned from historical conversions data: 
* brand signal variance
* brand length scale
* model signal variance
* model length scale
* Gaussian noise variance for observed variable *y*
  
Comparison between conversion rate estimated with two approached, simple ratio of conversions to clicks, and conversion ratio predicted with Gaussian Processs model:
   
![Conversion rate prediction plot](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/dsl_examples/conversionrate/conversion_rate_prediction_plot.png "Conversion rate prediction plot")

## Code example ([source code](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/dsl/demo/conversionrate/ConversionRateTest.scala))
 
Create data model (item convertion rates):

```scala
 val conversionRates = List(
    ConversionRate(Item("Butterfly", "Grubba"), 1, 1),
    ConversionRate(Item("Butterfly", "Maze"), 10, 4),
    ConversionRate(Item("Tibhar", "Samsonov"), 10, 1),
    ConversionRate(Item("Tibhar", "Stratus"), 1, 1))

  val items = conversionRates.map(_.item)
```

Build Gaussian process model:


```scala
  val itemPopularityMean = Matrix.zeros(items.size, 1)
  
  val itemPopularityCovFunc = ItemPopularityCovFunc(
  brandLogSf = log(1), brandLogEll = log(1), modelLogSf = log(0.5),     modelLogEll = log(1))
  val itemPopularityCov = itemPopularityCovFunc.covarianceMatrix(items)
  val itemPopularitiesVariable = MultivariateGaussian(itemPopularityMean, itemPopularityCov)

    val convertionVariables = conversionRates.zipWithIndex.flatMap {
      case (c, index) =>

        val converted = (1 to c.conversions).map { i => MvnGaussianThreshold(itemPopularitiesVariable, index, exceeds = Some(true)) }
        val landedOff = (1 to (c.clicks - c.conversions)).map { i => MvnGaussianThreshold(itemPopularitiesVariable, index, exceeds = Some(false)) }

        List(converted, landedOff)
    }
```

Predict item similarities and conversion probabilities:


```scala  
  //Predict item popularities
  val itemPopularitiesMarginal = infer(itemPopularitiesVariable)

  //Predict and print item conversion probabilities
  println("brand,model,convertionRatio,convertionProbMean,convertionProbStdDev")
  conversionRates.zipWithIndex.foreach { case (conversionRate, index) =>
    val conversion = MvnGaussianThreshold(itemPopularitiesMarginal.copy(), index)
    val conversionProb = infer(conversion)
    println("%s,%s,%.2f,%.2f,%.2f".format(
    conversionRate.item.brand, conversionRate.item.model, conversionRate.conversions.toDouble / conversionRate.clicks,
    conversionProb.m, sqrt(conversionProb.v)))
  }
```