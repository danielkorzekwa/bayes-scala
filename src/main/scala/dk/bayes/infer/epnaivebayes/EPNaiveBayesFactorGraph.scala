package dk.bayes.infer.epnaivebayes

import scala.annotation.tailrec
import com.typesafe.scalalogging.slf4j.Logging

/**
 * Computes posterior of X for a naive bayes net. Variables: X, Y1|X, Y2|X,...Yn|X
 *
 * It run Expectation Propagation algorithm. http://en.wikipedia.org/wiki/Expectation_propagation
 *
 * @author Daniel Korzekwa
 */
case class EPNaiveBayesFactorGraph[X, Y](bn: EPBayesianNet[X, Y]) extends Logging {
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
      sendMsgs()

      if (bn.isIdentical(posterior, currPosterior, threshold)) return
      else calibrateIter(posterior, iterNum + 1)
    }

    calibrateIter(bn.prior, 1)
  }

  private def sendMsgs() {

    msgsUp = msgsUp.zip(bn.likelihoods).map {
      case (currMsgUp, llh) =>
        val cavity = divide(posterior, currMsgUp)
        val newMsgUp = try {

          val marginalX = calcMarginalX(cavity, llh)

          marginalX match {
            case Some(marginalX) => {
              posterior = marginalX
              divide(posterior, cavity)
            }
            case None => currMsgUp
          }

        } catch {
          case e: Exception => {
            logger.warn("Error computing newFactorUpMsg", e)
            currMsgUp
          }
        }

        newMsgUp
    }
  }

}