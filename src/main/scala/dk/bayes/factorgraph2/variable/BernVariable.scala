package dk.bayes.factorgraph2.variable

import dk.bayes.factorgraph2.api.Variable

case class BernVariable(k: Int) extends Variable[Double] {
  require(k == 1 || k == 0, s"k=${k}. Bernoulli k parameters must be in {0,1}")

  def calcVariable(): Double = ???
}