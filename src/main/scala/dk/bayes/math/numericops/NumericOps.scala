package dk.bayes.math.numericops

trait NumericOps[THIS] {

  this: THIS =>

  def *[THAT](that: THAT)(implicit multOp: multOp[THIS, THAT]): THIS = multOp(this, that)

  def /[THAT](that: THAT)(implicit divideOp: divideOp[THIS, THAT]): THIS = divideOp(this, that)

}