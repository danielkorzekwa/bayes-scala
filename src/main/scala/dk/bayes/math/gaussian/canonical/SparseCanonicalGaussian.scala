package dk.bayes.math.gaussian.canonical

import breeze.linalg.CSCMatrix
import breeze.linalg.SparseVector

/**
 * @author Daniel Korzekwa
 */
case class SparseCanonicalGaussian(k: CSCMatrix[Double], h: SparseVector[Double], g: Double) extends CanonicalGaussian {
}