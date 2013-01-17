Plotting Gaussians
==================

This page presents some examples on plotting Gaussian distributions:

* Univariate Gaussian N(x|mu, sigma)
* Multivariate Gaussian N(x|mu, sigma)
* Linear Gaussian N(x|Ax + b, sigma)


Plotting Gaussians with Octave
------------------------------

### Univariate Gaussian

	% Plotting N(x|mu, sigma)
	function plotnormpdf()
	
	  mu = 3
	  sigma= 1.5
	
	  x=linspace(mu-4*sigma,mu+4*sigma,100);
	  y=normpdf(x,3,1.5);
	   
	  plot(x,y)
	  xlabel('x')
	  ylabel('pdf(x)')
	end

![Univariate Gaussian](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/plotting_gaussian/octave_univariate_gaussian.png "Univariate Gaussian")

### Multivariate Gaussian

	% Required package: http://octave.sourceforge.net/statistics/
	% Plotting N(x|mu, sigma)
	function plotmvnpdf()
	
	  mu = [3 1.7];
	  sigma = [1.5 -0.15 ;-0.15 0.515];
	
	  x=linspace(mu(1)-4*sigma(1,1),mu(1)+4*sigma(1,1),100);
	  y=linspace(mu(2)-4*sigma(2,2),mu(2)+4*sigma(2,2),100);         
	   
	  for i=1:length(y)
	    for j=1:length(x)
		  z(i,j) = mvnpdf([x(j) y(i)],mu,sigma);
	    end
	  end
	  
	  mesh(x,y,z)
	  xlabel('x')
	  ylabel('y')
	  zlabel('pdf(x,y)')
	end

![Multivariate Gaussian](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/plotting_gaussian/octave_multivariate_gaussian.png "Multivariate Gaussian")

![Multivariate Gaussian](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/plotting_gaussian/octave_multivariate_gaussian_contour.png "Multivariate Gaussian")

### Linear Gaussian

	% Plotting N(x|Ax + b, sigma)
	function plotcondpdf()
	
	  A = 0.9;
	  b = 0.1;
	  sigma = 1.5;
	  
	  x=linspace(-5,5,100);
	  y=linspace(-5,5,100);        
	   
	  for i=1:length(y)
	    for j=1:length(x)
		  z(i,j) = normpdf(y(i),A*x(j)+b,sigma);
	    end
	  end
	  
	  mesh(x,y,z); shading interp
	  xlabel('x')
	  ylabel('y')
	  zlabel('pdf(x,y)')
	end

![Linear Gaussian](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/plotting_gaussian/octave_linear_gaussian.png "Linear Gaussian")
![Linear Gaussian](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/plotting_gaussian/octave_linear_gaussian_contour.png "Linear Gaussian")

Plotting Gaussians with GnuPlot
-------------------------------
In progress...
