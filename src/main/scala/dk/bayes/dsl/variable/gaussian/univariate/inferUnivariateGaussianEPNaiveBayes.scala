package dk.bayes.dsl.variable.gaussian.univariate

import dk.bayes.dsl.InferEngine
import dk.bayes.infer.epnaivebayes.inferPosterior
import dk.bayes.math.gaussian.Gaussian
import dk.bayes.math.gaussian.canonical.CanonicalGaussian
import dk.bayes.math.linear.Matrix
import scala.math._
import dk.bayes.dsl.factor.DoubleFactor
import dk.bayes.infer.epnaivebayes.inferPosterior

object inferUnivariateGaussianEPNaiveBayes extends InferEngine[UnivariateGaussian, UnivariateGaussian] {

  def isSupported(x: UnivariateGaussian): Boolean = {

    !x.hasParents &&
      x.getChildren.size > 0 &&
      x.getChildren.filter(c => c.hasChildren).size == 0 &&
      x.getChildren.filter(c => !c.isInstanceOf[DoubleFactor[_, _]]).size == 0
  }

  def infer(x: UnivariateGaussian): UnivariateGaussian = {

    val prior = x
    val likelihoods = x.getChildren.map(c => c.asInstanceOf[DoubleFactor[Gaussian, _]])

    val posterior = inferPosterior(prior, likelihoods)
    UnivariateGaussian(posterior.m, posterior.v)
  }

}