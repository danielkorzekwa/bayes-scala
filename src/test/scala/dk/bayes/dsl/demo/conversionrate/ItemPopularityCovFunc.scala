package dk.bayes.dsl.demo.conversionrate

import breeze.linalg.DenseMatrix
import dk.bayes.math.covfunc.CovSEiso
import breeze.linalg._

case class ItemPopularityCovFunc(brandLogSf: Double, brandLogEll: Double, modelLogSf: Double, modelLogEll: Double) {

  private val brandCov = CovSEiso(brandLogSf, brandLogEll)
  private val modelCov = CovSEiso(modelLogSf, modelLogEll)

  def covariance(item1: Item, item2: Item): Double = {

    val (item1BrandVector, item2BrandVector) = if (item1.brand.equals(item2.brand)) (Array(1d), Array(1d)) else (Array(1d, 0d), Array(0d, 1d))
    val (item1ModelVector, item2ModelVector) = if (item1.model.equals(item2.model)) (Array(1d), Array(1d)) else (Array(1d, 0d), Array(0d, 1d))

    val covValue = brandCov.cov(item1BrandVector, item2BrandVector) + modelCov.cov(item1ModelVector, item2ModelVector)
    covValue
  }

  def covarianceMatrix(items: Seq[Item]): DenseMatrix[Double] = {
    val m = DenseMatrix.tabulate(items.size, items.size)((rowIndex, colIndex) => covariance(items(rowIndex), items(colIndex))) + DenseMatrix.eye[Double](items.size) * 1e-9
    m
  }
}