package dk.bayes.math

import scala.io.Source
import java.io.InputStream

package object linear {

  implicit def doubleToLinearDouble(d: Double): LinearDouble = new LinearDouble(d)
  class LinearDouble(d: Double) {
    def *(m: Matrix): Matrix = m * d
  }

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
}