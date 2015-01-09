package dk.bayes.dsl.demo.variables

import dk.bayes.math.gaussian.LinearGaussian
import dk.bayes.math.gaussian.Proj

object project {

  def apply(q: dk.bayes.math.gaussian.Gaussian, w: Double, a: Double, x: Double): dk.bayes.math.gaussian.Gaussian = {
    //Z(m,v) = (1-w)*Zterm1 + w*Zterm2
    val Zterm1 = (q * LinearGaussian(1, 0, 1)).marginalise(0)
    val Zterm2 = (q * LinearGaussian(0, 0, a)).marginalise(0)

    val Z = (1 - w) * Zterm1.pdf(x) + w * Zterm2.pdf(x)
    val dZ_m = (1 - w) * Zterm1.derivativeM(x)
    val dZ_v = (1 - w) * Zterm1.derivativeV(x)

    val projM = Proj.projMu(q, Z, dZ_m)
    val projV = Proj.projSigma(q, Z, dZ_m, dZ_v)

    dk.bayes.math.gaussian.Gaussian(projM, projV)
  }
}