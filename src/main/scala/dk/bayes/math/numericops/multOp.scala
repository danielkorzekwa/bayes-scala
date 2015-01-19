package dk.bayes.math.numericops

trait multOp[A, B] {
  def apply(a: A, b: B):A
}