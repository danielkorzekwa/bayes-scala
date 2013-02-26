Gaussian approximation with moment matching, aka proj() operator in Expectation Propagation (IN PROGRESS)
====================================================

<p style="vertical-align:middle">

Moment matching is a technique for approximating a function ![p(x)][eq1] with a Gaussian distribution ![p_new(x)][eq2] by matching expectations 
![E[x]][eq3] and ![E[xx]][eq4].

![Ep[x],Ep[x^2]][eq5] 

</p>
Then the mean and the variance of Gaussian approximation are defined by

![mean, variance][eq6]

Following Thomas Minka [1](#references) and Kevin P. Murphy [2](#references), in a specific case of a function ![p(x) = 1/Z * f(x)q(x))][eq7], where ![p(x) ~ N(mu,sigma)][eq8] and ![Z(m,v)=normalisation constant][eq9], 
,it can be shown that:

![Ep[x],Ep[xx],mu_pnew,variance_pnew][eq10]

More generally, for any member of exponential family [3](#references) ![p(x|eta) = h(x)g(eta)exp{eta*u(x)}][eq11], moments can be computed by differentiating 
the log partition function ![(ln g(eta))'][eq12].

Example - Clutter Problem
-------------------------

.....

![Moment matching][f1] 

References
----------

1. Thomas P Minka. A family of algorithms for approximate Bayesian inference, 2001
2. Kevin P. Murphy. From Belief Propagation to Expectation Propagation , 2001
3. Exponential Family, http://en.wikipedia.org/wiki/Exponential_family

[eq1]: https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/moment_matching/eq1.png "p(x)"
[eq2]: https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/moment_matching/eq2.png "p_new(x)"
[eq3]: https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/moment_matching/eq3.png "E[x]"
[eq4]: https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/moment_matching/eq4.png "E[xx]"
[eq5]: https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/moment_matching/eq5.png "Ep[x],Ep[x^2]"
[eq6]: https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/moment_matching/eq6.png "mean, variance"
[eq7]: https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/moment_matching/eq7.png "p(x) = 1/Z * f(x)q(x))"
[eq8]: https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/moment_matching/eq8.png "p(x) ~ N(mu,sigma)"
[eq9]: https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/moment_matching/eq9.png "Z(m,v)=normalisation constant"
[eq10]: https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/moment_matching/eq10.png "Ep[x],Ep[xx],mu_pnew,variance_pnew"
[eq11]: https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/moment_matching/eq11.png "p(x|eta) = h(x)g(eta)exp{eta*u(x)}"
[eq12]: https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/moment_matching/eq12.png "(ln g(eta))'"

[l1]: http://en.wikipedia.org/wiki/Exponential_family#Moments_and_cumulants_of_the_sufficient_statistic "Exponential Family"

[f1]: https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/moment_matching/moment_matching_plot.png "Moment matching"