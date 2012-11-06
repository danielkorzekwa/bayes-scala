package dk.bayes.em
import scala.io.Source

/**
 * Represents data samples, which are used for learning parameters of Bayesian Network.
 *
 * @author Daniel Korzekwa
 *
 * @param variableIds Defines the order of variables in a single sample
 *
 * @param samples Single sample contains values for all variables in a Bayesian Network
 * 
 * Use -1 to encode unknown value for a variable.
 *
 */
class DataSet(val variableIds: Array[Int], val samples: Array[Array[Int]])

object DataSet {
  def apply(variableIds: Array[Int], samples: Array[Array[Int]]): DataSet =
    new DataSet(variableIds, samples)

  def fromFile(filePath: String, variableIds: Array[Int]): DataSet = {

    val source = Source.fromFile(filePath)

    val samples: Iterator[Array[Int]] = source.getLines().drop(1).map { line =>
      val lineSplit = line.split(",")
      val sample: Array[Int] = lineSplit.map(v => v.toInt)
      sample
    }

    DataSet(variableIds, samples.toArray)
  }
}