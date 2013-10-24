

class MyObserver(MarketObserver):
    def update(self, ss, when):
        strLine = Long.toString(when.getTimeInMillis()).encode('utf-8')
        if 'X' in ss:
            strLine = strLine + ',' \
                + Double.toString(market.getLastPrice(0,'X-OPEN'))\
                        .encode('utf-8') + ','\
                + Double.toString(market.getLastPrice(0,'X-HIGH'))\
                        .encode('utf-8') + ',' \
                + Double.toString(market.getLastPrice(0,'X-LOW'))\
                        .encode('utf-8') + ','\
                + Double.toString(market.getLastPrice(0,'X-CLOSE'))\
                        .encode('utf-8') + ','\
                + Long.toString(market.getLastVolume(0,'X'))\
                        .encode('utf-8')
        else:
            strLine = strLine + ',-,-,-,-,-'

        if 'Y' in ss:
            strLine = strLine + ',' \
                + Double.toString(market.getLastPrice(0,'Y-OPEN'))\
                        .encode('utf-8') + ','\
                + Double.toString(market.getLastPrice(0,'Y-HIGH'))\
                        .encode('utf-8') + ',' \
                + Double.toString(market.getLastPrice(0,'Y-LOW'))\
                        .encode('utf-8') + ','\
                + Double.toString(market.getLastPrice(0,'Y-CLOSE'))\
                        .encode('utf-8') + ','\
                + Long.toString(market.getLastVolume(0,'Y'))\
                        .encode('utf-8')
        else:
            strLine = strLine + ',-,-,-,-,-'
 
        print strLine
                

xSource = CSVGeneral.getInstance().simpleContinuousSingleSource('X', \
    'XAGUSD1.csv', 'yyyy.MM.dd,HH:mm', \
    '([0-9]{4}.[0-9]{2}.[0-9]{2},[0-9]{2}:[0-9]{2}),(.*),(.*),(.*),(.*),(.*)', \
    [Fields.DATE,Fields.OPEN,Fields.HIGH,Fields.LOW,Fields.CLOSE,Fields.VOLUME])
ySource = CSVGeneral.getInstance().simpleContinuousSingleSource('Y', \
    'XAGUSD1.csv', 'yyyy.MM.dd,HH:mm', \
    '([0-9]{4}.[0-9]{2}.[0-9]{2},[0-9]{2}:[0-9]{2}),(.*),(.*),(.*),(.*),(.*)', \
    [Fields.DATE,Fields.OPEN,Fields.HIGH,Fields.LOW,Fields.CLOSE,Fields.VOLUME])

source = CompossedSource([xSource, ySource])


market = RandomAccessMarket(0.0, 5000)
source.addMarketListener(market)
source.addListener(MyObserver())

strLine = 'milliseconds'
for s in ['X', 'Y']:
    strLine = strLine + ',' + s + '-OPEN'
    strLine = strLine + ',' + s + '-HIGH'
    strLine = strLine + ',' + s + '-LOW'
    strLine = strLine + ',' + s + '-CLOSE'
    strLine = strLine + ',' + s + '-Volume'

print strLine

source.run()


