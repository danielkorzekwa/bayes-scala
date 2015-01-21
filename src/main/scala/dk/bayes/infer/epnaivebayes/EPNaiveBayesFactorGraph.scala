package dk.bayes.infer.epnaivebayes

import scala.annotation.tailrec
import com.typesafe.scalalogging.slf4j.Logging
import dk.bayes.dsl.factor.DoubleFactor
import dk.bayes.dsl.factor.SingleFactor
import dk.bayes.math.numericops._

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
case class EPNaiveBayesFactorGraph[X](prior: SingleFactor[X], likelihoods: Seq[DoubleFactor[X, _]], paralllelMessagePassing: Boolean = false)(implicit val multOp: multOp[X, X], val divideOp: divideOp[X, X], val isIdentical: isIdentical[X, X]) extends Logging {

  private var msgsUp: Seq[X] = likelihoods.map(l => l.initFactorMsgUp)
  private var posterior = msgsUp.foldLeft(prior.factorMsgDown)((posterior, msgUp) => multOp(posterior, msgUp))

  def getPosterior(): X = posterior

  def calibrate(maxIter: Int = 100, threshold: Double = 1e-4) {

    @tailrec
    def calibrateIter(currPosterior: X, iterNum: Int) {
      if (iterNum >= maxIter) {
        logger.warn(s"Factor graph did not converge in less than ${maxIter} iterations")
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

    posterior = msgsUp.foldLeft(prior.factorMsgDown)((posterior, msgUp) => multOp(posterior, msgUp))
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