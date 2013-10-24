##
## Maximum variations with given realized probability 
## for the given filename of CSV data.
##
## Parameters:
##       symbol name;
##       csv file complete path;
##       size of period to evaluate variations;
##
##
## Expected data format is:
##   yyyy.MM.dd,hh:mm,open,high,low,close,volume
##

symbol = args[0]
fileName = args[1]
periods = Integer.parseInt(args[2])

source = CSVGeneral.getInstance().simpleContinuousSingleSource(symbol, \
    fileName, 'yyyy.MM.dd,HH:mm', \
    '([0-9]{4}.[0-9]{2}.[0-9]{2},[0-9]{2}:[0-9]{2}),(.*),(.*),(.*),(.*),(.*)', \
    [Fields.DATE,Fields.OPEN,Fields.HIGH,Fields.LOW,Fields.CLOSE,Fields.VOLUME])



# Bind 
class VariancesObserver(MarketObserver):
    def __init__(self, market):
        self.numOfSignals = 0
        self.listHighVariancesPeriod = []
        self.listLowVariancesPeriod = []
        self.listLowVariancesIntraPeriod = []
        self.listHighVariancesIntraPeriod = []
        self.market = market

    def update(self, ss, when):
        self.numOfSignals = self.numOfSignals + 1

        if self.numOfSignals > periods :
            refPrice = self.market.getLastPrice(periods, symbol + '-OPEN')

            # Look for extreme values into period
            lowVarianceIntraPeriod = 0
            highVarianceIntraPeriod = 0
            for i in range(periods):
                m = self.market.getLastPrice(i, symbol + '-LOW')    
                M = self.market.getLastPrice(i, symbol + '-HIGH')    
                if m<refPrice :
                    lowVarianceIntraPeriod = max(lowVarianceIntraPeriod, \
                            (refPrice - m)/refPrice)
                if M>refPrice :
                    highVarianceIntraPeriod = max(highVarianceIntraPeriod, \
                            (M - refPrice)/refPrice)

            # Calculate variance in the limits of period
            highVariancePeriod = max(0, (self.market.getLastPrice(0, \
                    symbol + '-HIGH') - refPrice)/refPrice)
            lowVariancePeriod = max(0, (refPrice - self.market.getLastPrice(\
                    0, symbol + '-LOW'))/refPrice) 

            self.listHighVariancesPeriod.append(highVariancePeriod)
            self.listLowVariancesPeriod.append(lowVariancePeriod)
            self.listLowVariancesIntraPeriod.append(lowVarianceIntraPeriod)
            self.listHighVariancesIntraPeriod.append(highVarianceIntraPeriod)


# Join everything together
market = SequentialAccessMarket(0.0, 5000)
variances = VariancesObserver(market)
source.addMarketListener(market)
source.addListener(variances)

# Ready, steady, go
source.run()

print 'Maximum low variance intra period :'
print max(variances.listLowVariancesIntraPeriod)
print 'Maximum high variance intra period :'
print max(variances.listHighVariancesIntraPeriod)
print 'Maximum low variance init and end of period :'
print max(variances.listLowVariancesPeriod)
print 'Maximum high variance init and end of period :'
print max(variances.listHighVariancesPeriod)

variances.listLowVariancesIntraPeriod.sort()
variances.listHighVariancesIntraPeriod.sort()
variances.listLowVariancesPeriod.sort()
variances.listHighVariancesPeriod.sort()

print '98% of high variances intra period are less than :'
print variances.listHighVariancesIntraPeriod[\
    98 * len(variances.listHighVariancesIntraPeriod) / 100]
print '98% of high variances from init to end of each period are less than :'
print variances.listHighVariancesPeriod[\
    98 * len(variances.listHighVariancesIntraPeriod) / 100]

