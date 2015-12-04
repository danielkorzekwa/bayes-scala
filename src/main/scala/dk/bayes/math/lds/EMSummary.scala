package dk.bayes.math.lds

import dk.bayes.math.gaussian.Gaussian

case class EMSummary(priorMean:Gaussian,emissionVar:Double,iterations:Int)