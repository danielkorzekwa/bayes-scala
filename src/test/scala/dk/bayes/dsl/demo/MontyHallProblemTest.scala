package dk.bayes.dsl.demo

import org.junit._
import Assert._
import dk.bayes.dsl.variable.Categorical
import dk.bayes.dsl.infer
import dk.bayes.testutil.AssertUtil._

class MontyHallProblemTest {

  val carDoor = Categorical(Vector(1d / 3, 1d / 3, 1d / 3))
  val guestDoor = Categorical(Vector(1d / 3, 1d / 3, 1d / 3))

  val montyDoor = Categorical(carDoor, guestDoor, Vector(
    0, 0.5, 0.5,
    0, 0, 1,
    0, 1, 0,
    0, 0, 1,
    0.5, 0, 0.5,
    1, 0, 0,
    0, 1, 0,
    1, 0, 0,
    0.5, 0.5, 0))

  @Test def test {

    guestDoor.setValue(0) //Guest chooses door 1
    montyDoor.setValue(1) //Monty opens door 2 

    assertVector(Array(1d / 3, 0, 2d / 3), infer(carDoor).cpd, 0.0001)
  }

}