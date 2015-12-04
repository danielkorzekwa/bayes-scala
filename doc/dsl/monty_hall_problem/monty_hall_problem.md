# Monty Hall problem

[Monty Hall problem] on wikipedia.
Bayesian Networks diagram was created with the [SamIam] tool. 

![Student Bayesian Network](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/dsl/monty_hall_problem/monty_hall_bn.png "Student Bayesian Network")

Compute the probabilities of winning a car given a guest chooses door 1 and Monty opens door 2
([source code](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/dsl/demo/MontyHallProblemTest.scala)):

```scala

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
    
    guestDoor.setValue(0) //Guest chooses door 0
    montyDoor.setValue(1) //Monty opens door 1 
    
    infer(carDoor) //List(1/3,0,2/3)

```

[SamIam]: http://reasoning.cs.ucla.edu/samiam/
[Monty Hall problem]: http://en.wikipedia.org/wiki/Monty_Hall_problem