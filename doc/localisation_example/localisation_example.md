1D robot localisation
====================

The task is to predict the location of a robot given noisy observations of its current position [1](#references). In a static localisation setup, 
it's assumed that robot is not moving while subsequent measurements of a robot location are taken. Whereas in a dynamic variant, robot's position 
is changing over the time. There are 4 different solutions to 1D location problem, including Kalman Filter [1](#references), 
Canonical Gaussian [2,4](#references), Bayes's theorem for Gaussian Variables [3](#references) and Expectation Propagation [3,5](#references).

Static localisation
-------------------

![Static localisation 1D](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/localisation_example/static_localisation_1d.png "Static localisation 1D")

Figure 1 Probabilistic graphical model for static robot localisation.


Robot location:

![Prior prob](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/localisation_example/static_prior_prob.png "Prior prob")

Measured robot position given its current position:

![Emission prob](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/localisation_example/static_emission_prob.png "Emission prob")

Predicted robot position given observed position:

![Posterior](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/localisation_example/static_posterior.png "Posterior")

![Prior, likelihood and posterior distribution](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/localisation_example/static_localisation_gaussian.png "Prior, likelihood and posterior distribution")

Figure 2 Prior, likelihood and posterior distributions

* Implementation
  * [Kalman Filter](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/gaussian/localisation1d/StaticLocalisationKalmanTest.scala)
  * [Canonical Gaussian](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/gaussian/localisation1d/StaticLocalisationCanonicalGaussianTest.scala)
  * [Bayes's theorem for Gaussian Variables](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/gaussian/localisation1d/StaticLocalisationGaussianTest.scala)
  * [Expectation Propagation](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/gaussian/localisation1d/StaticLocalisationEPTest.scala)
 

Dynamic localisation
--------------------

![Dynamic localisation 1D](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/localisation_example/dynamic_localisation_1d.png "Dynamic localisation 1D")

Figure 1 Probabilistic graphical model for dynamic robot localisation.

Robot location at the time t0:

![Prior prob](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/localisation_example/dynamic_prior_prob.png "Prior prob")

Robot location at the time t1 given its location at the time t0:

![Transition prob](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/localisation_example/dynamic_transition_prob.png "Transition prob")

Measured robot position given its current location:

![Emission prob](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/localisation_example/dynamic_emission_prob.png "Emission prob")

Predicted robot location given observed position:

![Posterior](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/localisation_example/dynamic_posterior.png "Posterior")

* Implementation
 * [Kalman Filter](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/gaussian/localisation1d/HMMLocalisationKalmanTest.scala)
 * [Canonical Gaussian](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/gaussian/localisation1d/HMMLocalisationCanonicalGaussianTest.scala)
 * [Bayes's theorem for Gaussian](https://github.com/danielkorzekwa/bayes-scala/blob/master/src/test/scala/dk/bayes/gaussian/localisation1d/HMMLocalisationGaussianTest.scala)
 
References
---------------
1. Stuart Russell, Peter Norvig. Artificial Intelligence - A Modern Approach, Third Edition, Chapter 15.4.2 A simple one-dimensional example, 2010
2. Daphne Koller, Nir Friedman. Probabilistic Graphical Models, Principles and Techniques, 2009
3. Christopher M. Bishop. Pattern Recognition and Machine Learning (Information Science and Statistics), 2009
4. Kevin P. Murphy. A Variational Approximation for Bayesian Networks with Discrete and Continuous Latent Variables, 1999
5. Thomas P Minka. Expectation Propagation for Approximate Bayesian Inference, 2004