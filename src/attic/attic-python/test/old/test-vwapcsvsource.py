##
## Expected data format is:
##
##   Symbol,,dd-mmm-yyyy,HH:MM:ss.S,+2,Intraday 1Min,Open,High,Low,Last,Vol,
##   AveragePrice,VWAP,NumberOfTrades,OpenBid,HighBid,LowBid,CloseBid,
##   NumberOfBids,OpenAsk,HighAsk,LowAsk,CloseAsk,NumberOfAsks
##

symbol='TEF.MC'
fileName = 'TEF1min-N24033945.csv';

class MyObserver(MarketObserver):
    def update(self, ss, when):
        strLine = Long.toString(when.getTimeInMillis()).encode('utf-8')
        strLine = strLine + ',' \
            + Double.toString(market.getLastPrice(0,symbol + '-OPEN'))\
                    .encode('utf-8') + ','\
            + Double.toString(market.getLastPrice(0,symbol + '-HIGH'))\
                    .encode('utf-8') + ',' \
            + Double.toString(market.getLastPrice(0,symbol + '-LOW'))\
                    .encode('utf-8') + ','\
            + Double.toString(market.getLastPrice(0,symbol + '-CLOSE'))\
                    .encode('utf-8') + ','\
            + Long.toString(market.getLastVolume(0,symbol))\
                    .encode('utf-8')
        strLine = strLine + ',' \
            + Double.toString(market.getLastBid(0,symbol + '-OPEN'))\
                    .encode('utf-8') + ','\
            + Double.toString(market.getLastBid(0,symbol + '-HIGH'))\
                    .encode('utf-8') + ',' \
            + Double.toString(market.getLastBid(0,symbol + '-LOW'))\
                    .encode('utf-8') + ','\
            + Double.toString(market.getLastBid(0,symbol + '-CLOSE'))\
                    .encode('utf-8')
        strLine = strLine + ',' \
            + Double.toString(market.getLastAsk(0,symbol + '-OPEN'))\
                    .encode('utf-8') + ','\
            + Double.toString(market.getLastAsk(0,symbol + '-HIGH'))\
                    .encode('utf-8') + ',' \
            + Double.toString(market.getLastAsk(0,symbol + '-LOW'))\
                    .encode('utf-8') + ','\
            + Double.toString(market.getLastAsk(0,symbol + '-CLOSE'))\
                    .encode('utf-8')
        strLine = strLine + ',' \
            + Double.toString(market.getLastPrice(0,symbol + '-VWAP'))\
                    .encode('utf-8')

        print strLine
                

source = CSVGeneral.getInstance().vwapContinuousSingleSource(symbol, \
    fileName, 'dd-MMM-yyyy,HH:mm:ss.SSS', \
        '(.*),,([0-9]{2}-[A-Z]{3}-[0-9]{4},[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3},.*),Intraday 1Min,(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*)', \
         [Fields.IGNORE,Fields.DATE, Fields.OPEN,Fields.HIGH,Fields.LOW,Fields.CLOSE,Fields.VOLUME,Fields.IGNORE, Fields.IGNORE,Fields.IGNORE, Fields.IGNORE,Fields.IGNORE,Fields.IGNORE,Fields.IGNORE,Fields.IGNORE, Fields.IGNORE,Fields.IGNORE,Fields.IGNORE,Fields.IGNORE,Fields.IGNORE], \
         [Fields.IGNORE,Fields.IGNORE, Fields.IGNORE,Fields.IGNORE,Fields.IGNORE,Fields.IGNORE,Fields.IGNORE,Fields.IGNORE, Fields.IGNORE,Fields.IGNORE, Fields.OPEN,Fields.HIGH,Fields.LOW,Fields.CLOSE,Fields.IGNORE, Fields.IGNORE,Fields.IGNORE,Fields.IGNORE,Fields.IGNORE,Fields.IGNORE], \
         [Fields.IGNORE,Fields.IGNORE, Fields.IGNORE,Fields.IGNORE,Fields.IGNORE,Fields.IGNORE,Fields.IGNORE,Fields.IGNORE, Fields.IGNORE,Fields.IGNORE, Fields.IGNORE,Fields.IGNORE,Fields.IGNORE,Fields.IGNORE,Fields.IGNORE, Fields.OPEN,Fields.HIGH,Fields.LOW,Fields.CLOSE,Fields.IGNORE], 8);

if not source.test('TEF.MC,,01-OCT-2010,07:01:00.000,+2,Intraday 1Min,18.155,18.165,18.08,18.135,352468,18.1232,18.1235,189,18.12,18.155,18.08,18.135,305,18.155,18.165,18.09,18.15,305', 1, '01-OCT-2010,07:01:00.000,+2') \
    or not source.test('TEF.MC,,01-OCT-2010,07:01:00.000,+2,Intraday 1Min,18.155,18.165,18.08,18.135,352468,18.1232,18.1235,189,18.12,18.155,18.08,18.135,305,18.155,18.165,18.09,18.15,305', 2, '18.155') :
    print "Error, pattern does not match";


market = RandomAccessMarket(0.0, 5000)
source.addMarketListener(market)
source.addListener(MyObserver())

strLine = 'milliseconds'
strLine = strLine + ',' + symbol + '-OPEN'
strLine = strLine + ',' + symbol + '-HIGH'
strLine = strLine + ',' + symbol + '-LOW'
strLine = strLine + ',' + symbol + '-CLOSE'
strLine = strLine + ',' + symbol + '-Volume'
strLine = strLine + ',' + symbol + '-Ask-OPEN'
strLine = strLine + ',' + symbol + '-Ask-HIGH'
strLine = strLine + ',' + symbol + '-Ask-LOW'
strLine = strLine + ',' + symbol + '-Ask-CLOSE'
strLine = strLine + ',' + symbol + '-Bid-OPEN'
strLine = strLine + ',' + symbol + '-Bid-HIGH'
strLine = strLine + ',' + symbol + '-Bid-LOW'
strLine = strLine + ',' + symbol + '-Bid-CLOSE'
strLine = strLine + ',' + symbol + '-VWAP'


print strLine

source.run()
