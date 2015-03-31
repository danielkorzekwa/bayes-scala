package dk.bayes.dsl.demo.conversionrate

import dk.bayes.math.linear.Matrix
import dk.bayes.infer.gp.cov.CovSEiso

case class ItemPopularityCovFunc(brandLogSf: Double, brandLogEll: Double, modelLogSf: Double, modelLogEll: Double) {

  private val brandCov = CovSEiso(brandLogSf, brandLogEll)
  private val modelCov = CovSEiso(modelLogSf, modelLogEll)

  def covariance(item1: Item, item2: Item): Double = {

    val (item1BrandVector, item2BrandVector) = if (item1.brand.equals(item2.brand)) (Matrix(1), Matrix(1)) else (Matrix(1, 0), Matrix(0, 1))
    val (item1ModelVector, item2ModelVector) = if (item1.model.equals(item2.model)) (Matrix(1), Matrix(1)) else (Matrix(1, 0), Matrix(0, 1))

    val covValue = brandCov.cov(item1BrandVector, item2BrandVector) + modelCov.cov(item1ModelVector, item2ModelVector)
    covValue
  }

  def covarianceMatrix(items: Seq[Item]): Matrix = {
    val m = Matrix(items.size, items.size, (rowIndex, colIndex) => covariance(items(rowIndex), items(colIndex))) + Matrix.identity(items.size) * 1e-9
    m
  }
}