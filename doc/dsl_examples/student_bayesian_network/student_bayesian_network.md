# Student Bayesian Network

Bayesian Networks diagram was created with the [SamIam] tool. 
Student model was borrowed from the book of *Daphne Koller, Nir Friedman. Probabilistic Graphical Models, Principles and Techniques, 2009, page 53*   

![Student Bayesian Network](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/student_bn.png "Student Bayesian Network")

Infer marginal for *grade* variable
([source code](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/dsl/demo/StudentTest.scala)):

```scala
  val difficulty = Categorical(Vector(0.6, 0.4))
  val intelli = Categorical(Vector(0.7, 0.3))
  val grade = Categorical(intelli, difficulty, Vector(0.3, 0.4, 0.3, 0.05, 0.25, 0.7, 0.9, 0.08, 0.02, 0.5, 0.3, 0.2))
  val sat = Categorical(intelli, Vector(0.95, 0.05, 0.2, 0.8))
  val letter = Categorical(grade, Vector(0.1, 0.9, 0.4, 0.6, 0.99, 0.01))

  infer(grade) // List(0.3620, 0.2884, 0.3496)
```

Infer posterior for *grade* variable given *sat* is high
([source code](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/dsl/demo/StudentTest.scala)):


```scala
  sat.setValue(0)

  infer(grade) // List(0.2446, 0.3257, 0.4295)
```

[SamIam]: http://reasoning.cs.ucla.edu/samiam/