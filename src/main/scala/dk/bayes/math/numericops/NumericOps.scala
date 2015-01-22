package dk.bayes.math.numericops

trait NumericOps[THIS] {

  this: THIS =>

  def *(that: THIS)(implicit multOp: multOp[THIS]): THIS = multOp(this, that)

  def /(that: THIS)(implicit divideOp: divideOp[THIS]): THIS = divideOp(this, that)

}