package dk.bayes.dsl.variable.gaussian.infer

import dk.bayes.dsl.InferEngine
import dk.bayes.dsl.variable.gaussian.UnivariateGaussian
import dk.bayes.infer.epnaivebayes.inferPosterior
import dk.bayes.infer.epnaivebayes.EPBayesianNet
import dk.bayes.math.gaussian.Gaussian
import dk.bayes.dsl.variable.gaussian.UnivariateLinearGaussian
import dk.bayes.math.gaussian.CanonicalGaussian
import dk.bayes.math.linear.Matrix
import scala.math._
import dk.bayes.dsl.variable.gaussian.UnivariateLinearGaussian
import dk.bayes.dsl.factor.DoubleFactor

object inferUnivariateGaussianEPNaiveBayes extends InferEngine[UnivariateGaussian, UnivariateGaussian] {

  def isSupported(x: UnivariateGaussian): Boolean = {

    !x.hasParents &&
      x.getChildren.size > 0 &&
      x.getChildren.filter(c => c.hasChildren).size == 0
  }

  def infer(x: UnivariateGaussian): UnivariateGaussian = {

    val prior = x
    val likelihoods = prior.getChildren.map(c => c.asInstanceOf[DoubleFactor[UnivariateGaussian, _]])
    val bn = GaussianEPBayesianNet(prior, likelihoods)
    val posterior = inferPosterior(bn)
    posterior
  }

  case class GaussianEPBayesianNet(val prior: UnivariateGaussian, val likelihoods: Seq[DoubleFactor[UnivariateGaussian, _]]) extends EPBayesianNet[UnivariateGaussian, DoubleFactor[UnivariateGaussian, _]] {
    val initFactorMsgUp = new UnivariateGaussian(0, Double.PositiveInfinity)

    def product(x1: UnivariateGaussian, x2: UnivariateGaussian): UnivariateGaussian = {
      val product = Gaussian(x1.m, x1.v) * Gaussian(x2.m, x2.v)
      new UnivariateGaussian(product.m, product.v)
    }

    def divide(x1: UnivariateGaussian, x2: UnivariateGaussian): UnivariateGaussian = {
      val product = Gaussian(x1.m, x1.v) / Gaussian(x2.m, x2.v)
      new UnivariateGaussian(product.m, product.v)
    }

    def marginalX(x: UnivariateGaussian, y: DoubleFactor[UnivariateGaussian, _]): UnivariateGaussian = {
      val marginal = y.marginals(Some(x), None)._1.get
      marginal
    }

    def isIdentical(x1: UnivariateGaussian, x2: UnivariateGaussian, threshold: Double): Boolean = {
      abs(x1.m - x2.m) < threshold &&
        abs(x1.v - x2.v) < threshold &&
        x1.v > 0 && x2.v > 0
    }
  }

}