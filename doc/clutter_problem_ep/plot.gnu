#Plotting posterior probability as a function of current iteration

set grid
set xlabel 'Iteration number' font '1,10'

plot "plot.dat" using 1:2 with lines t 'Posterior mean'