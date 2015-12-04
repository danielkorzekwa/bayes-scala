package dk.bayes.factorgraph2.api

import scala.annotation.tailrec

object calibrate {

  /**
   * @return (isCalibrated,number of iterations)
   */
  def apply(calibrationStep: () => Unit, maxIter: Int, isCalibrated: => Boolean): (Boolean, Int) = {

    @tailrec
    def calibrateRec(iterNum: Int): (Boolean, Int) = {

      val calibrated = isCalibrated
      if (iterNum > maxIter || calibrated) return (calibrated, iterNum - 1)
      calibrationStep()
      calibrateRec(iterNum + 1)
    }

    calibrateRec(1)
  }
}