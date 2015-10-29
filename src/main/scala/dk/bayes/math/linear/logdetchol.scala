package dk.bayes.math.linear

import breeze.linalg.DenseMatrix
import breeze.linalg.diag
import breeze.linalg.sum
import breeze.numerics._

/**
 * Following up:
 * 
 * 1)
 * http://xcorr.net/2008/06/11/log-determinant-of-positive-definite-matrices-in-matlab/
 *
 * 2)
 * GPML:
 * Copyright (c) 2003, 2004, 2005, 2006 Neil D. Lawrence
 * logdet.m CVS version 1.4
 * 	logdet.m SVN version 22
 * 	last update 2007-11-03T14:26:20.000000Z
 */
object logdetchol {

  def apply(l: DenseMatrix[Double]): Double = 2 * sum(log(diag(l)))
}