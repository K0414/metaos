

class MyObserver(MarketObserver):
    def update(self, ss, when):
        strLine = Long.toString(when.getTimeInMillis()).encode('utf-8')
        for s in bricSymbols:
            if s in ss:
                strLine = strLine + ',' \
                    + Double.toString(market.getLastPrice(0,s+'-OPEN'))\
                            .encode('utf-8') + ','\
                    + Double.toString(market.getLastPrice(0,s+'-HIGH'))\
                            .encode('utf-8') + ',' \
                    + Double.toString(market.getLastPrice(0,s+'-LOW'))\
                            .encode('utf-8') + ','\
                    + Double.toString(market.getLastPrice(0,s+'-CLOSE'))\
                            .encode('utf-8') + ','\
                    + Long.toString(market.getLastVolume(0,s))\
                            .encode('utf-8')
            else:
                strLine = strLine + ',-,-,-,-,-'
       
        if 'XAG' in ss:
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
        else:
            strLine = strLine + ',-,-,-,-,-'
 
        print strLine
                

bricSymbols = [ '1288.HK' ];# '3988.HK', '0883.HK', '0939.HK', '2628.HK', '3968.HK', '0941.HK', '0688.HK', '0386.HK', '1088.HK', '0728.HK', '0762.HK', '1398.HK', '0857.HK', '2318.HK', '0700.HK', 'GAZPq.L', 'LKOHyq.L', 'NKELyq.L', 'NVTKq.L', 'RELIq.L', 'ROSNq.L', 'SNGSyq.L', 'TATNxq.L', 'BSBR.N', 'BBD.N', 'ABV.N', 'CIG.N', 'SID.N', 'GGB.N', 'HDB.N', 'IBN.N', 'ITUB.N', 'MBT.N', 'PBR.N', 'TNE.N', 'VALE.N', 'VIP.N', 'BIDU.OQ', 'INFY.OQ']
bricSource = CSVUnorderedData.getInstance().reuters('BRIC40_1min.csv', \
    bricSymbols)

xagSource = CSVGeneral.getInstance().simpleContinuousSingleSource('XAG', \
    'XAGUSD1.csv', 'yyyy.MM.dd,HH:mm', \
    '([0-9]{4}.[0-9]{2}.[0-9]{2},[0-9]{2}:[0-9]{2}),(.*),(.*),(.*),(.*),(.*)', \
    [Fields.DATE,Fields.OPEN,Fields.HIGH,Fields.LOW,Fields.CLOSE,Fields.VOLUME])

source = CompossedSource([bricSource, xagSource])


market = RandomAccessMarket(0.0, 5000)
source.addMarketListener(market)
source.addListener(MyObserver())

strLine = 'milliseconds'
for s in bricSymbols:
    strLine = strLine + ',' + s + '-OPEN'
    strLine = strLine + ',' + s + '-HIGH'
    strLine = strLine + ',' + s + '-LOW'
    strLine = strLine + ',' + s + '-CLOSE'
    strLine = strLine + ',' + s + '-Volume'

strLine = strLine + ',' + 'XAG-OPEN'
strLine = strLine + ',' + 'XAG-HIGH'
strLine = strLine + ',' + 'XAG-LOW'
strLine = strLine + ',' + 'XAG-CLOSE'
strLine = strLine + ',' + 'XAG-Volume'

print strLine

source.run()


