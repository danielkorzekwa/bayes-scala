#Plotting skills for both players before and after the game

set grid
set xlabel 'Skill'
set ylabel 'Pdf'
p (sigma)= 1/sqrt(2*pi*sigma**2)
gaussian(x,mu,sigma) = p(sigma) * exp(-(x-mu)**2/(2*sigma**2))

plot [-30:70][0:0.1] gaussian(x,4,sqrt(81)) t 'P1 before the game',\
gaussian(x,27.1743,sqrt(37.5013)) t 'P1 after the game',\
gaussian(x,41,sqrt(25)) t 'P2 before the game',\
gaussian(x,33.8460,sqrt(20.8610)) t 'P2 after the game'
