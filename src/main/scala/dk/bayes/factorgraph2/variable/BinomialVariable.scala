package dk.bayes.factorgraph2.variable

import dk.bayes.factorgraph2.api.Variable

case class BinomialVariable(k: Int,n:Int) extends Variable[Double] {
  
   def calcVariable(): Double = ???
}