package dk.bayes.math

import scala.io.Source
import java.io.InputStream
import org.ejml.ops.CommonOps
import org.ejml.simple.SimpleMatrix

package object linear {

  implicit def arrayToMatrix(values: Array[Array[Double]]):Matrix = Matrix(values)
  
  implicit def doubleToLinearDouble(d: Double): LinearDouble = new LinearDouble(d)
  class LinearDouble(d: Double) {
    def *(m: Matrix): Matrix = m * d
  }
  
  def sumRows(m:Matrix):Matrix = Matrix(new SimpleMatrix(CommonOps.sumRows(m.matrix.getMatrix(),null)))
  

  def loadCSV(inputStream: InputStream, skipLinesNum: Int): Matrix = {
    loadCSV(Source.fromInputStream(inputStream), skipLinesNum)
  }
  
  def loadCSV(file: String, skipLinesNum: Int): Matrix = {
    loadCSV(Source.fromFile(file), skipLinesNum)
  }

  def loadCSV(source: Source, skipLinesNum: Int): Matrix = {

    var rowNum = 0
    var colNum = -1

    val data = source.getLines.drop(skipLinesNum).flatMap { line =>

      val lineArray = line.split(",").map(v => v.toDouble)
      rowNum += 1
      require(colNum == -1 || colNum == lineArray.size)
      colNum = lineArray.size
      lineArray
    }.toArray

    Matrix(rowNum, colNum, data)
  }
  /**
   * http://en.wikipedia.org/wiki/Woodbury_matrix_identity
   */
  def woodbury(Ainv:Matrix,U:Matrix,Cinv:Matrix,V:Matrix):Matrix = {
    Ainv - Ainv*U*(Cinv + V*Ainv*U).inv*(V*Ainv)
  }
}