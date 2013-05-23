package dk.bayes.learn.lds

import org.junit._
import Assert._

class GenericLDSLearnTest {

  /**
   * Tests for M-step.
   */
  @Test(expected = classOf[IllegalArgumentException]) def single_time_slice {
    val data = LatentVariable(1.99966, 0.49020, None) :: Nil
  }

  @Test def two_time_slices {
    val data = LatentVariable(1.99966, 0.49020, None) :: LatentVariable(3.99959, 10.95929, Some(0.9808509)) :: Nil

    assertEquals(2.0002, GenericLDSLearn.newA(data), 0.0001)
    assertEquals(8.9966, GenericLDSLearn.newQ(data), 0.0001)
  }

  @Test def three_time_slices {
    val data = LatentVariable(1.99966, 0.49020, None) :: LatentVariable(3.99959, 10.95929, Some(0.9808509)) :: LatentVariable(8, 52.78515, Some(21.89761)) :: Nil

    assertEquals(1.9994, GenericLDSLearn.newA(data), 0.0001)
    assertEquals(9.0142, GenericLDSLearn.newQ(data), 0.0001)
  }

}