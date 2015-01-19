package dk.bayes.math.numericops

trait NumericOps[THIS] {

  def getThis(): THIS

  
  def *[THAT](that: THAT)(implicit multOp: multOp[THIS, THAT]): THIS = multOp(getThis(), that)
  
   def /[THAT](that: THAT)(implicit divideOp: divideOp[THIS, THAT]): THIS = divideOp(getThis(), that)

}