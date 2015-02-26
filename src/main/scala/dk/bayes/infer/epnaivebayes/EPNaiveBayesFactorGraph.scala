package dk.bayes.infer.epnaivebayes

import com.typesafe.scalalogging.slf4j.LazyLogging

import scala.annotation.tailrec
import dk.bayes.dsl.factor.DoubleFactor
import dk.bayes.dsl.factor.SingleFactor
import dk.bayes.math.numericops._
import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian
import dk.bayes.math.gaussian.canonical.SparseCanonicalGaussian
import dk.bayes.math.gaussian.canonical.SparseCanonicalGaussian
import dk.bayes.math.gaussian.Gaussian

/**
 * Computes posterior of X for a naive bayes net. Variables: X, Y1|X, Y2|X,...Yn|X
 *
 * It run Expectation Propagation algorithm. http://en.wikipedia.org/wiki/Expectation_propagation
 *
 * @param bn
 * @param paralllelMessagePassing If true then messages between X variable and Y variables are sent in parallel
 *
 * @author Daniel Korzekwa
 */
case class EPNaiveBayesFactorGraph[X](prior: SingleFactor[X], likelihoods: Seq[DoubleFactor[X, _]], paralllelMessagePassing: Boolean = false)(implicit val multOp: multOp[X], val divideOp: divideOp[X], val isIdentical: isIdentical[X]) extends LazyLogging {

  private var msgsUp: Seq[X] = likelihoods.map(l => l.initFactorMsgUp)
  private var posterior = multOp(prior.factorMsgDown, multOp(msgsUp: _*))

  def getPosterior(): X = posterior

  def calibrate(maxIter: Int = 100, threshold: Double = 1e-6) {

    @tailrec
    def calibrateIter(currPosterior: X, iterNum: Int) {
      if (iterNum >= maxIter) {      
        logger.warn(s"Factor graph did not converge in less than ${maxIter} iterations. Prior=%s, Posterior=%s".format(prior, posterior))
        return
      }
      if (paralllelMessagePassing) sendMsgsParallel() else sendMsgsSerial()

      if (isIdentical(posterior, currPosterior, threshold)) return
      else calibrateIter(posterior, iterNum + 1)
    }

    calibrateIter(posterior, 1)
  }

  private def sendMsgsParallel() {

    msgsUp = msgsUp.zip(likelihoods).map {
      case (currMsgUp, llh) =>

        val newMsgUp = llh.calcYFactorMsgUp(posterior, currMsgUp) match {
          case Some(msg) => msg
          case None      => currMsgUp
        }

        newMsgUp
    }

    posterior = multOp(prior.factorMsgDown, multOp(msgsUp: _*))
  }

  private def sendMsgsSerial() {

    msgsUp = msgsUp.zip(likelihoods).map {
      case (currMsgUp, llh) =>

        val newMsgUp = llh.calcYFactorMsgUp(posterior, currMsgUp) match {
          case Some(msg) => {
            val cavity = divideOp(posterior, currMsgUp)
            posterior = multOp(cavity, msg)
            msg
          }
          case None => currMsgUp
        }

        newMsgUp
    }
  }

}