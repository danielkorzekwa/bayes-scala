package dk.bayes.factorgraph2.variable

import dk.bayes.factorgraph2.api.Variable

case class BernVariable(p:Double) extends Variable[Double] {

  def calcVariable(): Double = p
}