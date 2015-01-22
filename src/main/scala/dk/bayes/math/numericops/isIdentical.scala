package dk.bayes.math.numericops

trait isIdentical[X] {

  def apply(x1:X,x2:X,tolerance:Double):Boolean
}