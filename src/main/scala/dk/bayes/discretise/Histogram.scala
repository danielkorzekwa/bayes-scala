package dk.bayes.discretise

/**
 * This class represents histogram. http://en.wikipedia.org/wiki/Histogram
 *
 * @author korzekwad
 */
case class Histogram(startValue: Double, endValue: Double, binsNum: Int) {

  def toValues(): Seq[Double] = mapValues(v => v)

  def mapValues[T](f: Double => T): Seq[T] = {
    val interval = (endValue - startValue) / (binsNum-1)
    
    val values = for (i <- startValue to endValue by interval) yield f(i)
    values
  }

}