
r <- read.csv('conversionrate.csv',header=T)

plot(r$convertionRatio,col='blue',ylim=c(0,1),xaxt="n",pch=15,xlab="Item",ylab="Conversion rate")
points(r$convertionProbMean,col='green',pch=15)

val x = 1:nrow(r)
segments(x,r$convertionProbMean-r$convertionProbStdDev , x,r$convertionProbMean+r$convertionProbStdDev)
segments(x-epsilon,r$convertionProbMean-r$convertionProbStdDev,x+epsilon,r$convertionProbMean-r$convertionProbStdDev)
segments(x-epsilon,r$convertionProbMean+r$convertionProbStdDev,x+epsilon,r$convertionProbMean+r$convertionProbStdDev)

axis(side=1,at=x,labels=paste(r$brand,"/",r$model) )

grid()

legend(x="top",c("Ratio of conversions to clicks","One-sigma interval for conversion rate given by Gaussian Process"), inset=0.03, cex=1, col=c("blue","green"),
       pch=c(15,15),bg="white")

title("Conversion rate for table tennis blades")