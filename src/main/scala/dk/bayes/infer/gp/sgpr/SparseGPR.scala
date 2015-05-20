package dk.bayes.infer.gp.sgpr

import dk.bayes.math.linear.Matrix

/**
 * Sparse Gaussian Process Regression
 * Variational Learning of Inducing Variables in Sparse Gaussian Processes (http://jmlr.org/proceedings/papers/v5/titsias09a/titsias09a.pdf)
 * Predicted mean/variance are the same as for Projected Processes method (Seeger et al. 2003)
 */
trait SparseGPR {

  /**
   * Joint model: p(f|u)*p(z|u)*p(u)*p(y|f)
   *
   *
   * Returns [mean,variance] for p(z|y)
   */
  def predict(z: Matrix): Matrix
}