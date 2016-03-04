package dk.bayes.math.accuracy

import org.junit._
import Assert._
import breeze.linalg.DenseMatrix
import dk.bayes.math.linear.isIdentical

class aggregateTest {

  val data = DenseMatrix((1.0, 2.0, 3.0, 4.0, 5.0, 6.0), (0d, 0d, 1d, 1d, 0d, 1d)).t

  @Test(expected = classOf[IllegalArgumentException]) def test_bins0 = {

    val aggr = aggregate(data, binNum = 0)
    assertTrue(isIdentical(DenseMatrix((2.0, 0.3333), (5.0, 0.6666)), aggr, 0.0001))
  }

  @Test def test_bins2 = {

    val aggr = aggregate(data, binNum = 2)
    assertTrue(isIdentical(DenseMatrix((2.0, 0.3333), (5.0, 0.6666)), aggr, 0.0001))
  }

  @Test def test_bins3 = {

    val aggr = aggregate(data, binNum = 3)

    assertTrue(isIdentical(DenseMatrix((1.5, 0.0), (3.5, 1.0), (5.5, 0.5)), aggr, 0.0001))
  }

  @Test def test_bins4 = {

    val aggr = aggregate(data, binNum = 4)
    println(aggr)
    assertTrue(isIdentical(DenseMatrix((1.5, 0.0), (3.5, 1.0), (5.5, 0.5)), aggr, 0.0001))
  }

  @Test def test_bins5 = {

    val aggr = aggregate(data, binNum = 5)
    assertTrue(isIdentical(DenseMatrix((1.5, 0.0), (3.5, 1.0), (5.5, 0.5)), aggr, 0.0001))
  }

  @Test def test_bins6 = {

    val aggr = aggregate(data, binNum = 6)
    assertTrue(isIdentical(DenseMatrix((1.0, 2.0, 3.0, 4.0, 5.0, 6.0), (0d, 0d, 1d, 1d, 0d, 1d)).t, aggr, 0.0001))
  }

  @Test def test_bins7 = {

    val aggr = aggregate(data, binNum = 7)
    assertTrue(isIdentical(DenseMatrix((1.0, 2.0, 3.0, 4.0, 5.0, 6.0), (0d, 0d, 1d, 1d, 0d, 1d)).t, aggr, 0.0001))
  }

}