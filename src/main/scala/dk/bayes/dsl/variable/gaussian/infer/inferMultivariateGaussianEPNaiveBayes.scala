package dk.bayes.dsl.variable.gaussian.infer

import dk.bayes.dsl.InferEngine
import dk.bayes.dsl.variable.gaussian.MultivariateGaussian
import dk.bayes.dsl.factor.DoubleFactor
import dk.bayes.infer.epnaivebayes.EPBayesianNet
import dk.bayes.math.linear.Matrix
import dk.bayes.math.gaussian.CanonicalGaussian
import scala.math._
import dk.bayes.infer.epnaivebayes.inferPosterior

object inferMultivariateGaussianEPNaiveBayes extends InferEngine[MultivariateGaussian, MultivariateGaussian] {

  def isSupported(x: MultivariateGaussian): Boolean = {

    !x.hasParents &&
      x.getChildren.size > 0 &&
      x.getChildren.filter(c => c.hasChildren).size == 0 &&
      x.getChildren.filter(c => !c.isInstanceOf[DoubleFactor[_, _]]).size == 0
  }

  def infer(x: MultivariateGaussian): MultivariateGaussian = {
    val prior = x
    val likelihoods = prior.getChildren.map(c => c.asInstanceOf[DoubleFactor[CanonicalGaussian, _]])
    val bn = GaussianEPBayesianNet(CanonicalGaussian(prior.m, prior.v), likelihoods)
    
    val posterior = inferPosterior(bn)
    MultivariateGaussian(posterior.mean, posterior.variance)
  }

  case class GaussianEPBayesianNet(val prior: CanonicalGaussian, val likelihoods: Seq[DoubleFactor[CanonicalGaussian, _]]) 
  extends EPBayesianNet[CanonicalGaussian, DoubleFactor[CanonicalGaussian, _]] {

    val initFactorMsgUp = CanonicalGaussian(Matrix.zeros(prior.h.size, 1), Matrix.identity(prior.h.size) * Double.PositiveInfinity)

    def product(x1: CanonicalGaussian, x2: CanonicalGaussian): CanonicalGaussian = x1*x2

    def divide(x1: CanonicalGaussian, x2: CanonicalGaussian):CanonicalGaussian= x1 / x2

    def calcMarginalX(x: CanonicalGaussian, y: DoubleFactor[CanonicalGaussian, _]): Option[CanonicalGaussian] = {
      val marginal = y.marginals(Some(x), None)._1.get

      Some(marginal)
    }

    def isIdentical(x1: CanonicalGaussian, x2: CanonicalGaussian, tolerance: Double): Boolean = {
      x1.mean.isIdentical(x2.mean, tolerance) &&
        x1.variance.isIdentical(x2.variance, tolerance)
    }
  }
}