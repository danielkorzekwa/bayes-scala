package dk.bayes.dsl.factor

trait SingleFactor[+X] {

  def factorMsgDown():X
}
