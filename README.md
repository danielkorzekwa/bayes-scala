# Bayesian Networks in Scala 

[![Join the chat at https://gitter.im/danielkorzekwa/bayes-scala](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/danielkorzekwa/bayes-scala?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/danielkorzekwa/bayes-scala.svg)](https://travis-ci.org/danielkorzekwa/bayes-scala)
[![Codacy Badge](https://www.codacy.com/project/badge/2a48694cabbe4cd386af1be55602cbbf)](https://www.codacy.com/public/danielkorzekwa/bayes-scala)

It is a Scala library for building Bayesian Networks with discrete/continuous variables and running deterministic Bayesian inference.

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

## Roadmap

Multi task Gaussian process regression

* [Gaussian Process Regression Networks](http://icml.cc/2012/papers/329.pdf)
* [Gaussian Process Regression Networks (section: Gaussian Process Regression Networks)](http://www.cs.cmu.edu/~andrewgw/)
* [Computationally Efficient Convolved Multiple Output Gaussian Processes](http://www.jmlr.org/papers/volume12/alvarez11a/alvarez11a.pdf)
* [Dependent Gaussian Processes](http://papers.nips.cc/paper/2561-dependent-gaussian-processes.pdf)
* [Multiple Output Gaussian Process Regression](http://citeseerx.ist.psu.edu/viewdoc/download;jsessionid=169DB9B94D0C1B16EDC68B55D8D92F6C?doi=10.1.1.114.3898&rep=rep1&type=pdf)
* [Efficient Variational Inference for Gaussian Process Regression Networks](file:///C:/daniel/nicta_publication_full_6462.pdf)
* [Multi-task Gaussian Process Prediction](http://papers.nips.cc/paper/3189-multi-task-gaussian-process-prediction.pdf)
* [mtgp toolbox](https://github.com/ebonilla/mtgp)

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