package dk.bayes.factorgraph2.variable

import dk.bayes.math.gaussian.canonical.CanonicalGaussian
import dk.bayes.factorgraph2.api.Variable

case class CanonicalGaussianVariable() extends Variable[CanonicalGaussian] {

  def calcVariable(): CanonicalGaussian = {
    val tailProduct =  CanonicalGaussian.multOp(getMessages().tail.map(_.get): _*)
    CanonicalGaussian.multOp(getMessages().head.get,tailProduct)
  }
}