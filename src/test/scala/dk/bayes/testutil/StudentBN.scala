package dk.bayes.testutil

import dk.bayes.factor._
import dk.bayes.factor.Factor._

/**
 * Bayesian network example, borrowed from 'Daphne Koller, Nir Friedman. Probabilistic Graphical Models, Principles and Techniques, 2009'
 *
 * @author Daniel Korzekwa
 */
case class StudentBN {

  //Create variables
  val difficultyVar = Var(1, 2)
  val intelliVar = Var(2, 2)
  val gradeVar = Var(3, 3)
  val satVar = Var(4, 2)
  val letterVar = Var(5, 2)

  //Create factors
  val difficultyFactor = Factor(difficultyVar, Array(0.6, 0.4))
  val intelliFactor = Factor(intelliVar, Array(0.7, 0.3))
  val gradeFactor = Factor(intelliVar, difficultyVar, gradeVar, Array(0.3, 0.4, 0.3, 0.05, 0.25, 0.7, 0.9, 0.08, 0.02, 0.5, 0.3, 0.2))
  val satFactor = Factor(intelliVar, satVar, Array(0.95, 0.05, 0.2, 0.8))
  val letterFactor = Factor(gradeVar, letterVar, Array(0.1, 0.9, 0.4, 0.6, 0.99, 0.01))
}