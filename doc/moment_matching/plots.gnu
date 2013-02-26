p(sigma)= 1/sqrt(2*pi*sigma**2)
gaussian(x,mu,sigma) = p(sigma) * exp(-(x-mu)**2/(2*sigma**2))
linear_gaussian(x,y,sigma) = p(sigma) * exp(-(y-(x))**2/(2*sigma**2))

a = 10
b=100
prior=15
x=3
w = 0.4
Z = (1 - w) * gaussian(x,prior, sqrt(b + 1)) + w * gaussian(x,0, sqrt(a))

set title 'Moment matching' font '1,12'
plot [-25:50][0:0.125] \
((1-w)*linear_gaussian(theta,x,1)*gaussian(theta,prior,sqrt(b)) + w*gaussian(x,0,sqrt(a))*gaussian(theta,prior,sqrt(b)))/Z t 'p(x) = 1/Z * f(x)q(x)', \
gaussian(theta,11.83649,sqrt(101.215)) t 'p_new(x) = N(11.836,101.215)'
