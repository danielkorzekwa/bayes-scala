set samples 10000
set title '1D static localisation' font '1,12'
plot [-4:8] gaussian(theta,3,sqrt(1.5)) t 'Current location N(3,1.5)',linear_gaussian(theta,0.6,sqrt(0.9)) t 'Observed location N(0.6,0.9)',gaussian(theta,1.5,sqrt(0.5625)) t 'Location after 1 observation N(1.5,0.5625)',\
gaussian(theta,1.1538,sqrt(0.3461))  t 'Location after 2 observations N(1.1538,0.3461)',gaussian(theta,0.8571,sqrt(0.1607))  t 'Location after 5 observations N(0.8571,0.1607)'

#set title '1D dynamic localisation' font '1,12'
#plot [-4:8] gaussian(theta,3,sqrt(1.5)) t 'Location at the time t0  N(3,1.5)',gaussian(theta,3,sqrt(1.7)) t 'Location at the time t1 N(3,1.7)',\
#linear_gaussian(theta,0.6,sqrt(0.9)) t 'Observed location N(0.6,0.9)',gaussian(theta,1.430,sqrt(0.588)) t 'Location at the time t1 given observation N(1.43,0.588)'