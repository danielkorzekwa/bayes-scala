package dk.bayes.learn.lds

import org.junit._
import Assert._
import dk.bayes.math.linear._
import dk.bayes.math.gaussian.canonical.CanonicalGaussian
import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian
import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector

class GenericLDSMStepTest {

  /**
   * Tests for M-step (learning C and R)
   *
   */
  @Test def mstep_for_c_and_r_single_stat:Unit = {
    val data1 = (DenseCanonicalGaussian(3, 1), 5d)

    assertEquals(1.5, GenericLDSLearn.newC(Vector(data1)), 0.0001)
    assertEquals(2.5, GenericLDSLearn.newR(Vector(data1)), 0.0001)
  }

  @Test def mstep_for_c_and_r_single_stat_zero_variance:Unit = {
    val data1 = (DenseCanonicalGaussian(3, 0.00001), 3d)

    assertEquals(1, GenericLDSLearn.newC(Vector(data1)), 0.0001)
    assertEquals(0, GenericLDSLearn.newR(Vector(data1)), 0.0001)
  }

  @Test def mstep_for_c_and_rmultiple_stats:Unit = {
    val data1 = (DenseCanonicalGaussian(3.5, 0.00001), 2d)
    val data2 = (DenseCanonicalGaussian(3.5, 0.00001), 3d)
    val data3 = (DenseCanonicalGaussian(3.5, 0.00001), 4d)
    val data4 = (DenseCanonicalGaussian(3.5, 0.00001), 5d)

    val data = Vector(data1, data2, data3, data4)

    assertEquals(0.9999, GenericLDSLearn.newC(data), 0.0001)
    assertEquals(1.250, GenericLDSLearn.newR(data), 0.0001)
  }

  @Test def mstep_for_c_and_rmultiple_stats2:Unit = {
    val data1 = (DenseCanonicalGaussian(6.5, 0.7), 2d)
    val data2 = (DenseCanonicalGaussian(6.5, 0.7), 3d)
    val data3 = (DenseCanonicalGaussian(6.5, 0.7), 4d)
    val data4 = (DenseCanonicalGaussian(6.5, 0.7), 5d)

    val data = Vector(data1, data2, data3, data4)

    assertEquals(0.5296, GenericLDSLearn.newC(data), 0.0001)
    assertEquals(1.4496, GenericLDSLearn.newR(data), 0.0001)
  }

  /**
   * Tests for M-step (learning A and Q) - single sequence of latent variables.
   */
  @Test def mstep_for_a_and_q_single_stat:Unit = {
    val data = DenseCanonicalGaussian(DenseVector(1.99966, 3.99959), new DenseMatrix(2, 2, Array(0.49020, 0.9808509, 0.9808509, 10.95929)))

    assertEquals(2.0002, GenericLDSLearn.newA(Vector(data)), 0.0001)
    assertEquals(8.9966, GenericLDSLearn.newQ(Vector(data)), 0.0001)
  }

  @Test def mstep_for_a_and_q_two_stats:Unit = {
    val data1 = DenseCanonicalGaussian(DenseVector(1.99966, 3.99959), new DenseMatrix(2, 2, Array(0.49020, 0.9808509, 0.9808509, 10.95929)))
    val data2 = DenseCanonicalGaussian(DenseVector(3.99959, 8), new DenseMatrix(2, 2, Array(10.95929, 21.89761, 21.89761, 52.78515)))
    val data = Vector(data1, data2)

    assertEquals(1.9994, GenericLDSLearn.newA(data), 0.0001)
    assertEquals(9.0142, GenericLDSLearn.newQ(data), 0.0001)
  }

  @Test def mstep_for_a_and_q_four_stats:Unit = {

    val data1 = DenseCanonicalGaussian(DenseVector(1.99966, 3.99959), new DenseMatrix(2, 2, Array(0.49020, 0.9808509, 0.9808509, 10.95929)))
    val data2 = DenseCanonicalGaussian(DenseVector(3.99959, 8), new DenseMatrix(2, 2, Array(10.95929, 21.89761, 21.89761, 52.78515)))
    val data3 = DenseCanonicalGaussian(DenseVector(2.99966, 6.99959), new DenseMatrix(2, 2, Array(0.49020, 0.9808509, 0.9808509, 10.95929)))
    val data4 = DenseCanonicalGaussian(DenseVector(6.99959, 12), new DenseMatrix(2, 2, Array(10.95929, 21.89761, 21.89761, 52.78515)))
    val data = Vector(data1, data2, data3, data4)

    assertEquals(1.8906, GenericLDSLearn.newA(data), 0.0001)
    assertEquals(9.9621, GenericLDSLearn.newQ(data), 0.0001)
  }

  /**
   * Tests for pi and V (prior parameters: mean and variance)
   */

  @Test(expected = classOf[IllegalArgumentException]) def learn_prior_mean_no_variables:Unit = {
    val data = Vector()
    GenericLDSLearn.newPi(data)
  }

  @Test(expected = classOf[IllegalArgumentException]) def learn_prior_variance_no_variables:Unit = {
    val data = Vector()
    GenericLDSLearn.newV(data)
  }

  @Test def learn_prior_parameters_single_variable:Unit = {
    val data = Vector(DenseCanonicalGaussian(m = 2, v = 0.5))

    val pi = GenericLDSLearn.newPi(data)
    val V = GenericLDSLearn.newV(data)

    assertEquals(2, pi, 0.0001)
    assertEquals(0.5, V, 0.0001)
  }

  @Test def learn_prior_parameters_multiple_variables:Unit = {
    val data = Vector(
      DenseCanonicalGaussian(m = 2, v = 0.5),
      DenseCanonicalGaussian(m = 3, v = 0.1),
      DenseCanonicalGaussian(m = 8, v = 0.35))

    val pi = GenericLDSLearn.newPi(data)
    val V = GenericLDSLearn.newV(data)

    assertEquals(4.3333, pi, 0.0001)
    assertEquals(7.2056, V, 0.0001)
  }

  @Test def learn_prior_parameters_two_the_same_variables:Unit = {
    val data = Vector(
      DenseCanonicalGaussian(m = 3, v = 0.5),
      DenseCanonicalGaussian(m = 3, v = 0.5))

    val pi = GenericLDSLearn.newPi(data)
    val V = GenericLDSLearn.newV(data)

    assertEquals(3, pi, 0.0001)
    assertEquals(0.5, V, 0.0001)
  }

}