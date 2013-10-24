
interpreteR = R(["correlation.r"])

interpreteR.eval("corre<-correlator()")
for i in range(1,200):
    interpreteR.eval("corre$memo(" + str(i) + "," + str(i) + ")")

print interpreteR.eval("corre$show()")

interpreteR.end()
