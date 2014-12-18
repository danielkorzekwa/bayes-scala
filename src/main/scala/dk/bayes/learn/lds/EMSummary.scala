package dk.bayes.learn.lds

import dk.bayes.math.gaussian.Gaussian

case class EMSummary(priorMean:Gaussian,emissionVar:Double,iterations:Int)