##
## Interface for ETS and FORECAST functions in R.
##
## Parameters:
##       symbol name;
##       csv file complete path;
##
## Expected data format is:
##   yyyy.MM.dd,hh:mm,open,high,low,close,volume
##

symbol = args[0]
fileName = args[1]
periods = Integer.parseInt(args[2])

source = CSVGeneral.getInstance().continuousSingleSource(symbol, \
    fileName, 'yyyy.MM.dd,HH:mm', \
    '([0-9]{4}.[0-9]{2}.[0-9]{2},[0-9]{2}:[0-9]{2}),(.*),(.*),(.*),(.*),(.*)', \
    [Fields.DATE,Fields.OPEN,Fields.HIGH,Fields.LOW,Fields.CLOSE,Fields.VOLUME])

interpreteR = R('forecastAdaptor.r')
interpreteR.eval('predictor <- etsPredictor()')


# Bind 
class ETSObserver(MarketObserver):
    def __init__(self, market):
        self.market = market

    def update(self, ss, when):
        interpreteR.eval('predictor$learn(' \
            + str(market.getLastPrice(0, symbol + '-CLOSE')) + ')')


# Join everything together
market = SequentialAccessMarket(0.0, 5000)
ets = ETSObserver(market)
source.addMarketListener(market)
source.addListener(ets)

# Ready, steady, go
source.run()

interpreteR.eval('predictor$forecast()')
interpreteR.eval('predictor$plot()')

# Land down
interpreteR.end()
