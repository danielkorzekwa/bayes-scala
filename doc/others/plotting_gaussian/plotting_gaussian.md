Plotting Gaussians
==================

This page presents some examples on plotting Gaussian distributions using both Octave [1](#references) and Gnuplot [2](#references) tools:

* Univariate Gaussian: N(x|mu, sigma) [3](#references) 
* Multivariate Gaussian: N(x|mu, sigma) [4](#references) [5](#references)
* Linear Gaussian: N(x|Ax + b, sigma) [6](#references)


Plotting Gaussians with Octave
------------------------------

### Univariate Gaussian

	% Plotting N(x|mu, sigma)
	function plotnormpdf()
	
	  mu = 3
	  sigma= 1.5
	
	  x=linspace(mu-4*sigma,mu+4*sigma,100);
	  y=normpdf(x,mu,sigma);
	   
	  plot(x,y)
	  xlabel('x')
	  ylabel('pdf(x)')
	end

![Univariate Gaussian](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/others/plotting_gaussian/octave_univariate_gaussian.png "Univariate Gaussian")

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

![Multivariate Gaussian](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/others/plotting_gaussian/octave_multivariate_gaussian.png "Multivariate Gaussian")

![Multivariate Gaussian](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/others/plotting_gaussian/octave_multivariate_gaussian_contour.png "Multivariate Gaussian")

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

![Linear Gaussian](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/others/plotting_gaussian/octave_linear_gaussian.png "Linear Gaussian")
![Linear Gaussian](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/others/plotting_gaussian/octave_linear_gaussian_contour.png "Linear Gaussian")

Plotting Gaussians with Gnuplot
-------------------------------

### Univariate Gaussian

	mu = 3
	sigma = 1.5
	
	p (sigma)= 1/sqrt(2*pi*sigma**2)
	gaussian(x,mu,sigma) = p(sigma) * exp(-(x-mu)**2/(2*sigma**2))
	
	plot [mu-4*sigma:mu+4*sigma] gaussian(x,mu,sigma)

![Univariate Gaussian](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/others/plotting_gaussian/gnuplot_univariate_gaussian.png "Univariate Gaussian")

### Multivariate Gaussian

	# http://mathworld.wolfram.com/BivariateNormalDistribution.html
	mu_x = 3
	mu_y = 1.7
	
	sigma_x = 1.5
	sigma_y = 0.515
	cov = -0.15
	
	z(x,y) = (x - mu_x)**2/sigma_x +  - 2*p*(x-mu_x)*(y-mu_y)/(sigma_x*sigma_y) + (y - mu_y)**2/sigma_y
	p = cov /(sigma_x*sigma_y)
	gaussian(x,y) = 1 / (2*pi*sigma_x*sigma_y*sqrt(1-p**2)) * exp(-z(x,y)/(2*(1-p**2)))
	
	set xrange [mu_x-4*sigma_x:mu_x+4*sigma_x]
	set yrange [mu_y-4*sigma_y:mu_y+4*sigma_y]
	set xlabel "x"
	set ylabel "y"
	
	set xyplane relative  0.2
	set pm3d at b
	set isosample 50
	#set pm3d map
	
	splot gaussian(x,y)

![Multivariate Gaussian](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/plotting_gaussian/gnuplot_multivariate_gaussian.png "Multivariate Gaussian")

![Multivariate Gaussian](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/plotting_gaussian/gnuplot_multivariate_gaussian_contour.png "Multivariate Gaussian")

### Linear Gaussian

	A = 0.9
	b = 0.1
	sigma = 1.5
	
	p(sigma) = 1/sqrt(2*pi*sigma**2)
	linear_gaussian(x,y,sigma) = p(sigma) * exp(-(y-(A*x+b))**2/(2*sigma**2))
	
	set xlabel "x"
	set ylabel "y"
	
	set xyplane relative  0.2
	set pm3d at b
	set isosample 50
	#set pm3d map
	
	splot [-5:5] [-5:5] linear_gaussian(x,y,sigma)

![Linear Gaussian](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/plotting_gaussian/gnuplot_linear_gaussian.png "Linear Gaussian")
![Linear Gaussian](https://raw.github.com/danielkorzekwa/bayes-scala/master/doc/plotting_gaussian/gnuplot_linear_gaussian_contour.png "Linear Gaussian")

References
---------------
1. Octave - http://www.gnu.org/software/octave/
2. Gnuplot - http://www.gnuplot.info/
3. Univariate Gaussian - http://en.wikipedia.org/wiki/Normal_distribution
4. Multivariate Gaussian - http://en.wikipedia.org/wiki/Multivariate_Gaussian
5. Bivariate Gaussian - http://mathworld.wolfram.com/BivariateNormalDistribution.html
6. Christopher M. Bishop. Pattern Recognition and Machine Learning (Information Science and Statistics), 2009
