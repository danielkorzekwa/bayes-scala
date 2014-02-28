package dk.bayes.infer.gp

import dk.bayes.math.gaussian.Linear._
import scala.math._

object GenericGPRegression extends GPRegression {

  def predict(x: Matrix, y: Matrix, z: Matrix, covFunc: CovFunc,noiseVar:Double): Matrix = {

    val xz = x.combine(x.numRows, 0, z)
    
    val k = Matrix(xz.numRows, xz.numRows, (rowIndex: Int, colIndex: Int) => covFunc.cov(xz.row(rowIndex).t, xz.row(colIndex).t)) + noiseVar*Matrix.identity(xz.numRows)
println(Matrix(xz.numRows, xz.numRows, (rowIndex: Int, colIndex: Int) => covFunc.cov(xz.row(rowIndex).t, xz.row(colIndex).t)))
    val kXX = k.extractMatrix(0, x.numRows, 0, x.numRows) 

    val kXZ = k.extractMatrix(0, x.numRows, x.numRows, k.numCols)

    val kZZ = k.extractMatrix(x.numRows, k.numRows, x.numRows, k.numCols)

    val kXXInv = kXX.inv
    val predMean = kXZ.t * (kXXInv * y)
    val predVar = kZZ - kXZ.t * kXXInv * kXZ

    predMean.combine(0, 1, predVar.extractDiag)
  }
}