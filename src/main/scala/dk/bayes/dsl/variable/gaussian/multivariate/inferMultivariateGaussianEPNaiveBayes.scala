package dk.bayes.dsl.variable.gaussian.multivariate

import dk.bayes.dsl.InferEngine
import dk.bayes.math.linear.Matrix
import dk.bayes.math.gaussian.canonical.CanonicalGaussian
import scala.math._
import dk.bayes.infer.epnaivebayes.inferPosterior
import scala.reflect.runtime.universe._
import scala.reflect.ClassTag
import dk.bayes.dsl.factor.DoubleFactor
import dk.bayes.infer.epnaivebayes.inferPosterior
import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian

object inferMultivariateGaussianEPNaiveBayes extends InferEngine[MultivariateGaussian, MultivariateGaussian] {

  def isSupported(x: MultivariateGaussian): Boolean = {

    !x.hasParents &&
      x.getChildren.size > 0 &&
      x.getChildren.filter(c => c.hasChildren).size == 0 &&
      x.getChildren.filter(c => !c.isInstanceOf[DoubleFactor[_, _]]).size == 0
  }

  def infer(x: MultivariateGaussian): MultivariateGaussian = {

    val prior = x
    val likelihoods = x.getChildren.map(c => c.asInstanceOf[DoubleFactor[CanonicalGaussian, _]])

    val posterior = inferPosterior(prior, likelihoods, paralllelMessagePassing = true,maxIter=200).asInstanceOf[DenseCanonicalGaussian]

    MultivariateGaussian(posterior.mean, posterior.variance)
  }

}