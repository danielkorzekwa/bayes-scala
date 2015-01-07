package dk.bayes.dsl.factor


/**
 * Factor for y = f(x)
 * 
 * @author Daniel Korzekwa
 */
trait DoubleFactor[X, Y] {

  /**
   * Returns (x,y) variable marginals for the product of x*factor*y
   */
  def marginals(x: Option[X], y: Option[Y]): (Option[X], Option[Y])
}