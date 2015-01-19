package dk.bayes.dsl.factor

import dk.bayes.math.gaussian.CanonicalGaussian

trait SingleFactor[X] {

  val factorMsgDown:X
}
