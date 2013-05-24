package dk.bayes.learn.lds

import org.junit._
import Assert._

class GenericLDSLearnTest {

  /**
   * Tests for M-step - single sequence of latent variables.
   */
  @Test(expected = classOf[IllegalArgumentException]) def newA_single_time_slice {
    val data = LatentVariable(1.99966, 0.49020, None) :: Nil
    GenericLDSLearn.newA(List(data))
  }

  @Test(expected = classOf[IllegalArgumentException]) def newQ_single_time_slice {
    val data = LatentVariable(1.99966, 0.49020, None) :: Nil
    GenericLDSLearn.newQ(List(data))
  }

  @Test def two_time_slices {
    val data = LatentVariable(1.99966, 0.49020, None) :: LatentVariable(3.99959, 10.95929, Some(0.9808509)) :: Nil

    assertEquals(2.0002, GenericLDSLearn.newA(List(data)), 0.0001)
    assertEquals(8.9966, GenericLDSLearn.newQ(List(data)), 0.0001)
  }

  @Test def three_time_slices {
    val data = LatentVariable(1.99966, 0.49020, None) :: LatentVariable(3.99959, 10.95929, Some(0.9808509)) :: LatentVariable(8, 52.78515, Some(21.89761)) :: Nil

    assertEquals(1.9994, GenericLDSLearn.newA(List(data)), 0.0001)
    assertEquals(9.0142, GenericLDSLearn.newQ(List(data)), 0.0001)
  }

  /**
   * Tests for M-step - multiples sequences of latent variables.
   */
  @Test(expected = classOf[IllegalArgumentException]) def mult_seq_newA_single_time_slice {
    val data1 = LatentVariable(1.99966, 0.49020, None) :: LatentVariable(3.99959, 10.95929, Some(0.9808509)) :: Nil
    val data2 = LatentVariable(1.99966, 0.49020, None) :: Nil
    GenericLDSLearn.newA(List(data1, data2))
  }

  @Test(expected = classOf[IllegalArgumentException]) def mult_seq_newQ_single_time_slice {
    val data1 = LatentVariable(1.99966, 0.49020, None) :: LatentVariable(3.99959, 10.95929, Some(0.9808509)) :: Nil
    val data2 = LatentVariable(1.99966, 0.49020, None) :: Nil
    GenericLDSLearn.newQ(List(data1, data2))
  }

  @Test def mult_seq_two_time_slices {
    val data1 = LatentVariable(1.99966, 0.49020, None) :: LatentVariable(3.99959, 10.95929, Some(0.9808509)) :: Nil
    val data2 = LatentVariable(3.99959, 10.95929, Some(0.9808509)) :: LatentVariable(8, 52.78515, Some(21.89761)) :: Nil

    assertEquals(1.9994, GenericLDSLearn.newA(List(data1, data2)), 0.0001)
    assertEquals(9.0142, GenericLDSLearn.newQ(List(data1, data2)), 0.0001)
  }

  @Test def mult_seq_three_time_slices {
    val data1 = LatentVariable(1.99966, 0.49020, None) :: LatentVariable(3.99959, 10.95929, Some(0.9808509)) :: LatentVariable(8, 52.78515, Some(21.89761)) :: Nil
    val data2 = LatentVariable(2.99966, 0.49020, None) :: LatentVariable(6.99959, 10.95929, Some(0.9808509)) :: LatentVariable(12, 52.78515, Some(21.89761)) :: Nil

    assertEquals(1.8906, GenericLDSLearn.newA(List(data1, data2)), 0.0001)
    assertEquals(9.9621, GenericLDSLearn.newQ(List(data1, data2)), 0.0001)
  }

}