package dk.bayes.model.clustergraph.factor

/**
 * Represents factor variable.
 *
 * @author Daniel Korzekwa
 *
 * @param id Unique variable identifier
 * @param dim Number of values that this variable can take on
 *
 */
case class Var(id: Int, dim: Int)
