package dk.bayes.math.numericops

trait multOp[X] {

  def apply(x1: X, x2: X): X
  def apply(x: X*): X
}