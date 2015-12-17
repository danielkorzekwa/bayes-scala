package dk.bayes.factorgraph2.variable

import dk.bayes.math.gaussian.canonical.CanonicalGaussian
import dk.bayes.factorgraph2.api.Variable

case class CanonicalGaussianVariable(name:String="na") extends Variable[CanonicalGaussian] {

  def calcVariable(): CanonicalGaussian = {
    
    val msgs = getMessages()
    val tailProduct =  CanonicalGaussian.multOp(msgs.tail.map(_.get): _*)
    CanonicalGaussian.multOp(msgs.head.get,tailProduct)
  }
}