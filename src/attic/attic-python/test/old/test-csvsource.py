

class MyObserver(MarketObserver):
    def update(self, ss, when):
        strLine = Long.toString(when.getTimeInMillis()).encode('utf-8')
        strLine = strLine + ',' \
            + Double.toString(market.getLastPrice(0,'XAG-OPEN'))\
                    .encode('utf-8') + ','\
            + Double.toString(market.getLastPrice(0,'XAG-HIGH'))\
                    .encode('utf-8') + ',' \
            + Double.toString(market.getLastPrice(0,'XAG-LOW'))\
                    .encode('utf-8') + ','\
            + Double.toString(market.getLastPrice(0,'XAG-CLOSE'))\
                    .encode('utf-8') + ','\
            + Long.toString(market.getLastVolume(0,'XAG'))\
                    .encode('utf-8')
        print strLine
                
source = CSVGeneral.getInstance().simpleContinuousSingleSource('XAG', \
    'XAGUSD1.csv', 'yyyy.MM.dd,HH:mm', \
    '([0-9]{4}.[0-9]{2}.[0-9]{2},[0-9]{2}:[0-9]{2}),(.*),(.*),(.*),(.*),(.*)', \
    [Fields.DATE,Fields.OPEN,Fields.HIGH,Fields.LOW,Fields.CLOSE,Fields.VOLUME])


market = RandomAccessMarket(0.0, 5000)
source.addMarketListener(market)
source.addListener(MyObserver())

strLine = 'milliseconds'
strLine = strLine + ',' + 'XAG-OPEN'
strLine = strLine + ',' + 'XAG-HIGH'
strLine = strLine + ',' + 'XAG-LOW'
strLine = strLine + ',' + 'XAG-CLOSE'
strLine = strLine + ',' + 'XAG-Volume'
print strLine

source.run()
