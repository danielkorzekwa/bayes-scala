package dk.bayes.math.discretise

/**
 * This class represents histogram. http://en.wikipedia.org/wiki/Histogram
 *
 * @author korzekwad
 */
case class Histogram(startValue: Double, endValue: Double, binsNum: Int) {

  private val interval = (endValue - startValue) / (binsNum - 1)

  /**
   * Returns values for all bins of the histogram.
   */
  def toValues(): Seq[Double] = mapValues(v => v)

  /**
   * Maps all values for all histogram bins to a sequence of objects
   *
   *  @param f Mapping function (Bin value) => Object in a sequence
   */
  def mapValues[T](f: Double => T): Seq[T] = {

    val values = for (i <- startValue to endValue by interval) yield f(i)
    values
  }

  /**
   * Returns the value for a given bin index. Bin index starts from 0.
   */
  def valueOf(binIndex: Int): Double = {
    require(binIndex >= 0 && binIndex < binsNum, "Bin index out of range")
    startValue + binIndex * interval
  }

  /**
   * Returns the bin index for a given value.  Bin index starts from 0.
   */
  def binIndexOf(value: Double): Int = {
    require(value >= startValue && value <= endValue, "Value out of range")

    var binIndex = 0
    var continue = true
    while (continue && binIndex < binsNum) {
      if (valueOf(binIndex) >= value) continue = false
      else binIndex+=1
    }
    binIndex
  }

}