Localisation example
====================

Task: 1D robot localisation [1](#references)

* Approach 1 - Kalman Filter [1](#references)
  * [Static localisation](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/gaussian/localisation1d/StaticLocalisationKalmanTest.scala)
  * [Dynamic localisation](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/gaussian/localisation1d/HMMLocalisationKalmanTest.scala)
* Approach 2 - Canonical Gaussian [2](#references)
  * [Static localisation](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/gaussian/localisation1d/StaticLocalisationCanonicalGaussianTest.scala)
  * [Dynamic localisation](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/gaussian/localisation1d/HMMLocalisationCanonicalGaussianTest.scala)
* Approach 3 - Bayes's theorem for Gaussian Variables [3](#references)
  * [Static localisation](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/gaussian/localisation1d/StaticLocalisationGaussianTest.scala)
  * [Dynamic localisation](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/gaussian/localisation1d/HMMLocalisationGaussianTest.scala)

References
---------------
1. Stuart Russell, Peter Norvig. Artificial Intelligence - A Modern Approach, Third Edition, Chapter 15.4.2 A simple one-dimensional example, 2010
2. Daphne Koller, Nir Friedman. Probabilistic Graphical Models, Principles and Techniques, 2009
3. Christopher M. Bishop. Pattern Recognition and Machine Learning (Information Science and Statistics), 2009