##
## Interface for FORECAST volatility using ARIMA.
##
## Parameters:
##       symbol name;
##       csv file complete path;
##
## Expected data format is:
##   Symbol,,dd-mmm-yyyy,HH:MM:ss.S,+2,Intraday 1Min,Open,High,Low,Last,Vol,
##   AveragePrice,VWAP,NumberOfTrades,OpenBid,HighBid,LowBid,CloseBid,
##   NumberOfBids,OpenAsk,HighAsk,LowAsk,CloseAsk,NumberOfAsks
##


##

symbol = args[0]
fileName = args[1]


source = CSVGeneral.getInstance().vwapContinuousSingleSource(symbol, \
    fileName, 'dd-MMM-yyyy,HH:mm:ss.SSS', \
    '(.*),,([0-9]{2}-[A-Z]{3}-[0-9]{4},[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3},.*),Intraday 1Min,(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*)', \
    [Fields.IGNORE,Fields.DATE, Fields.OPEN,Fields.HIGH,Fields.LOW,\
     Fields.CLOSE,Fields.VOLUME,Fields.IGNORE, Fields.IGNORE,Fields.IGNORE, \
     Fields.IGNORE,Fields.IGNORE,Fields.IGNORE,Fields.IGNORE,Fields.IGNORE, \
     Fields.IGNORE,Fields.IGNORE,Fields.IGNORE,Fields.IGNORE,Fields.IGNORE], \
    [Fields.IGNORE,Fields.IGNORE, Fields.IGNORE,Fields.IGNORE,Fields.IGNORE,\
     Fields.IGNORE,Fields.IGNORE,Fields.IGNORE,Fields.IGNORE,Fields.IGNORE, \
     Fields.OPEN,Fields.HIGH,Fields.LOW,Fields.CLOSE,Fields.IGNORE, \
     Fields.IGNORE,Fields.IGNORE,Fields.IGNORE,Fields.IGNORE,Fields.IGNORE], \
    [Fields.IGNORE,Fields.IGNORE, Fields.IGNORE,Fields.IGNORE,Fields.IGNORE,\
     Fields.IGNORE,Fields.IGNORE,Fields.IGNORE, Fields.IGNORE,Fields.IGNORE, \
     Fields.IGNORE,Fields.IGNORE,Fields.IGNORE,Fields.IGNORE,Fields.IGNORE, \
     Fields.OPEN,Fields.HIGH,Fields.LOW,Fields.CLOSE,Fields.IGNORE], 8);

if not source.test('TEF.MC,,01-OCT-2010,07:01:00.000,+2,Intraday 1Min,18.155,18.165,18.08,18.135,352468,18.1232,18.1235,189,18.12,18.155,18.08,18.135,305,18.155,18.165,18.09,18.15,305', 1, '01-OCT-2010,07:01:00.000,+2') \
  or not source.test('TEF.MC,,01-OCT-2010,07:01:00.000,+2,Intraday 1Min,18.155,18.165,18.08,18.135,352468,18.1232,18.1235,189,18.12,18.155,18.08,18.135,305,18.155,18.165,18.09,18.15,305', 2, '18.155') :
   print "Error, pattern does not match";



# Join everything together
market = RandomAccessMarket(0.0, 5000)
source.addMarketListener(market)

lastTime = source.last()


