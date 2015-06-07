# Can you please clarify for us: what is the future of bayes-scala?

* Is it your own project? 

Yes, I created it and I'm the main contributor, though some people did some work too.

* Is there company support behind it?

It is not officially supported by any company, but it is used at Betfair (company I work for) for some machine learning projects, and there are a few (maybe 10) people/companies I know about that used it for commercial or academic work.

* How much "future energy" does it have?

A lot:). I pretty much implement what I need for a company and some hobby ML projects. I truly love programming and machine learning, so I'm unlikely to stop working on it.

* What is the road map for 1 2 3 years?

Non-parametric probabilistic collaborative models. 

Gaussian Processes are cool, but in their simple form they are nowhere near to Neural Networks for multi output large scale prediction models and sharing the statistical strength between different outputs. But, there has been already some great work done in academia for building collaborative/convolved Gaussian Processes, including regression, classification and custom likelihood models.

The very next step is to implement Nguyen et al. Collaborative Multi-output Gaussian Processes, 2014 and compare it with independent GP model on Kaggle Walmart Weather Competition. A very simple GP got 5th place there, how much better the collaborative one might be?
