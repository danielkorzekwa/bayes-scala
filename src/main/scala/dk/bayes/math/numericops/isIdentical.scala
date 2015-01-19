package dk.bayes.math.numericops

trait isIdentical[A,B] {

  def apply(a:A,b:B,tolerance:Double):Boolean
}