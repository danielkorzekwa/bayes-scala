package dk.bayes.math.accuracy

import org.junit._
import Assert._
import breeze.linalg.DenseVector
import breeze.linalg.DenseMatrix

class loglikTest {
  
  @Test def test_binary = {
    
    val predicted = DenseVector(0.6d,0.3,0.2)
    val actual = DenseVector(1d,0,1d)
    assertEquals(-2.4769,loglik(predicted,actual),0.0001)
  }
  
   @Test def test_multiclass_two_classes = {
    
    val predicted = DenseMatrix((0.4,0.6),(0.7,0.3),(0.8,0.2))
    val actual = DenseVector(1d,0,1d)
    assertEquals(-2.4769,loglik(predicted,actual),0.0001)
  }
}