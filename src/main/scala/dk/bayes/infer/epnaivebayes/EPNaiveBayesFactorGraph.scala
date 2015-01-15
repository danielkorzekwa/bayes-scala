package dk.bayes.infer.epnaivebayes

import scala.annotation.tailrec
import com.typesafe.scalalogging.slf4j.Logging

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
case class EPNaiveBayesFactorGraph[X, Y](bn: EPBayesianNet[X, Y], paralllelMessagePassing: Boolean = false) extends Logging {
  import bn._

  private var msgsUp: Seq[X] = bn.likelihoods.map(l => bn.initFactorMsgUp)
  private var posterior = msgsUp.foldLeft(bn.prior)((posterior, msgUp) => product(posterior, msgUp))

  def getPosterior(): X = posterior

  def calibrate(maxIter: Int = 100, threshold: Double = 1e-4) {

    @tailrec
    def calibrateIter(currPosterior: X, iterNum: Int) {
      if (iterNum >= maxIter) {
        logger.warn(s"Factor graph did not converge in less than ${maxIter} iterations")
        return
      }
      if (paralllelMessagePassing) sendMsgsParallel() else sendMsgsSerial()

      if (bn.isIdentical(posterior, currPosterior, threshold)) return
      else calibrateIter(posterior, iterNum + 1)
    }

    calibrateIter(bn.prior, 1)
  }

  private def sendMsgsParallel() {

    msgsUp = msgsUp.zip(bn.likelihoods).map {
      case (currMsgUp, llh) =>

        val newMsgUp = calcYFactorMsgUp(posterior, currMsgUp, llh) match {
          case Some(msg) => msg
          case None => currMsgUp
        }

        newMsgUp
    }

    posterior = msgsUp.foldLeft(bn.prior)((posterior, msgUp) => product(posterior, msgUp))
  }

  private def sendMsgsSerial() {

    msgsUp = msgsUp.zip(bn.likelihoods).map {
      case (currMsgUp, llh) =>

        val newMsgUp = calcYFactorMsgUp(posterior, currMsgUp, llh) match {
          case Some(msg) => {
            val cavity = divide(posterior, currMsgUp)
            posterior = product(cavity, msg)
            msg
          }
          case None => currMsgUp
        }

        newMsgUp
    }
  }

}