package dk.bayes.learn.em
import scala.io.Source
import scala.Array.canBuildFrom

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
case class DataSet(variableIds: Array[Int], samples: Array[Array[Int]])

object DataSet {

  def fromFile(filePath: String, variableIds: Array[Int]): DataSet = {

    val source = Source.fromFile(filePath)

    val samples: Iterator[Array[Int]] = source.getLines().drop(1).map { line =>
      val lineSplit = line.split(",")
      val sample: Array[Int] = lineSplit.map(v => v.toInt)
      sample
    }

    DataSet(variableIds, samples.toArray)
  }

  /**
   * Returns sequence of evidence for a given sample and variable ids.
   *
   * @param variableIds Defines the order of variables in a sample
   *
   * @param sample Contains values for all variables in a Bayesian Network
   */
  def toEvidence(variableIds: Array[Int], sample: Array[Int]): Seq[Tuple2[Int, Int]] = {

    var evidence: List[Tuple2[Int, Int]] = List()

    var i = 0
    while (i < variableIds.size) {
      val varValue = sample(i)
      if (varValue >= 0) evidence = (variableIds(i), varValue) :: evidence
      i += 1
    }
    evidence
  }
}