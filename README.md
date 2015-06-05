# Bayesian Networks in Scala 

[![Join the chat at https://gitter.im/danielkorzekwa/bayes-scala](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/danielkorzekwa/bayes-scala?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/danielkorzekwa/bayes-scala.svg)](https://travis-ci.org/danielkorzekwa/bayes-scala)
[![Codacy Badge](https://www.codacy.com/project/badge/2a48694cabbe4cd386af1be55602cbbf)](https://www.codacy.com/public/danielkorzekwa/bayes-scala)

It is a Scala library for building Bayesian Networks with discrete/continuous variables and running deterministic Bayesian inference.

## Can you please clarify for us: what is the future of bayes-scala?

* Is it your own project? 

Yes, I created it and I'm the main contributor, thought some people did some work too.

* Is there company support behind it?

It is not officially supported by any company, but it is used at Betfair (company I work for) for some machine learning projects and there are a few (maybe 10) people/companies I know about that used it for commercial or academic work.

* How much "future energy" does it have?

A lot:). I pretty much implement what I need for company and some hobby ML projects. I truly love programming and machine learning, so I'm unlikely to stop working on it.

* What is the road map for 1 2 3 years?

Non-parametric probabilistic collaborative models. 

Gaussian Processes are cool, but in their simple form they are nowhere near to Neural Networks for multi output large scale prediction models and sharing the statistical strength between different outputs. But, there has been already some great work done in academia for building collaborative/convolved Gaussian Processes, including regression, classification and custom likelihood models.

The very next step is to implement Nguyen et al. Collaborative Multi-output Gaussian Processes, 2014 and compare it with independent GP model on Kaggle Walmart Weather Competition. A very simple GP got 5th place there, how much better the collaborative one might be?


## How to use it from sbt and maven?

Release and snaphot versions are available for Scala versions 2.10 and 2.11

### Release version

SBT configuration: 

```scala
libraryDependencies += "com.github.danielkorzekwa" % "bayes-scala_2.10" % "0.5"  
```

Maven configuration:

```scala  
  <dependencies>
    <dependency>
      <groupId>com.github.danielkorzekwa</groupId>
      <artifactId>bayes-scala_2.10</artifactId>
      <version>0.5</version>
    </dependency>
  <dependencies>
```

### Snapshot version

Snapshot artifact is built by a Travis CI and deployed to Sonatype OSS Snapshots repository with every commit to Bayes-scala project. 

With sbt build tool, add to build.sbt config file:

```scala
libraryDependencies += "com.github.danielkorzekwa" % "bayes-scala_2.10" % "0.6-SNAPSHOT"  

resolvers += Resolver.sonatypeRepo("snapshots")
```

With maven build tool, add to pom.xml config file:

```scala
  <repositories>
    <repository>
      <id>oss-sonatype-snapshots</id>
      <name>oss-sonatype-snapshots</name>
      <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
    </repository>
  </repositories>
  
  <dependencies>
    <dependency>
      <groupId>com.github.danielkorzekwa</groupId>
      <artifactId>bayes-scala_2.10</artifactId>
      <version>0.6-SNAPSHOT</version>
    </dependency>
  <dependencies>
```

## Examples

* Examples illustrating the usage of a high level API for building Bayesian Networks
  * [Student Bayesian Network](https://github.com/danielkorzekwa/bayes-scala/blob/master/doc/dsl_examples/student_bayesian_network/student_bayesian_network.md) 
  * [Monty Hall problem](https://github.com/danielkorzekwa/bayes-scala/blob/master/doc/dsl_examples/monty_hall_problem/monty_hall_problem.md)
  * [TrueSkill](https://github.com/danielkorzekwa/bayes-scala/blob/master/doc/dsl_examples/true_skill/true_skill.md)
  * [Clutter problem](https://github.com/danielkorzekwa/bayes-scala/blob/master/doc/dsl_examples/clutter_problem/clutter_problem.md) 
  * [Gaussian process regression](https://github.com/danielkorzekwa/bayes-scala/blob/master/doc/dsl_examples/gaussian_process_regression/gaussian_process_regression.md)
  * [Gaussian process regression with cluttered Gaussian likelihood](https://github.com/danielkorzekwa/bayes-scala/blob/master/doc/dsl_examples/gaussian_process_regression_cluttered_gaussian_likelihood/gaussian_process_regression_cluttered_gaussian_likelihood.md)
  * [1D Kalman filter] (https://github.com/danielkorzekwa/bayes-scala/blob/master/doc/dsl_examples/1d_kalman_filter/1d_kalman_filter.md)
  * [Conversion rate](https://github.com/danielkorzekwa/bayes-scala/blob/master/doc/dsl_examples/conversionrate/conversion_rate.md)

## Others

* [Low level algorithms] which are used behind the scenes for Bayesian Inference, e.g. Loopy Belief Propagation, Expectation Propagation, Variational Inference.

[Low level algorithms]: https://github.com/danielkorzekwa/bayes-scala/blob/master/doc/lowlevel/README.md