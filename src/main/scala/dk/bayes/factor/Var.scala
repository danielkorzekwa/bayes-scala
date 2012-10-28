package dk.bayes.factor

/**
 * Represents factor variable.
 *
 * @author Daniel Korzekwa
 *
 * @param id Unique variable identifier
 * @param dim Number of values that this variable can take on
 *
 */
class Var(val id: Int, val dim: Int)

object Var {

  /**
   * Creates factor variable.
   *
   * @param id Unique variable identifier
   * @param dim Number of values that this variable can take on
   */
  def apply(id: Int, dim: Int): Var = new Var(id, dim)
}