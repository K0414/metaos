
symbols = [ '1288.HK', '3988.HK', '0883.HK', '0939.HK', '2628.HK', '3968.HK', '0941.HK', '0688.HK', '0386.HK', '1088.HK', '0728.HK', '0762.HK', '1398.HK', '0857.HK', '2318.HK', '0700.HK', 'GAZPq.L', 'LKOHyq.L', 'NKELyq.L', 'NVTKq.L', 'RELIq.L', 'ROSNq.L', 'SNGSyq.L', 'TATNxq.L', 'BSBR.N', 'BBD.N', 'ABV.N', 'CIG.N', 'SID.N', 'GGB.N', 'HDB.N', 'IBN.N', 'ITUB.N', 'MBT.N', 'PBR.N', 'TNE.N', 'VALE.N', 'VIP.N', 'BIDU.OQ', 'INFY.OQ']

#lineProcessor = CSVReutersAdaptative('BRIC_1min.csv')
textFormat = MessageFormat("{0}")
dateFormat = SimpleDateFormat('dd-MMM-yyyy')
timeFormat = SimpleDateFormat('HH:mm:ss.SSS')
doubleFormat = DecimalFormat('#.##')

lineProcessor = CSVSourceLineProcessor([textFormat,dateFormat,timeFormat,None,None,doubleFormat,doubleFormat,doubleFormat,doubleFormat,doubleFormat,doubleFormat,doubleFormat,doubleFormat],[None,None,None,None,None,OPEN(PRICE),HIGH(PRICE),LOW(PRICE),CLOSE(PRICE),VOLUME(PRICE),Field.EXTENDED(PRICE,"Ave. Price"),Field.EXTENDED(PRICE,"VWAP"),Field.EXTENDED(PRICE,"No. Trades")],0,[1,2])

source = SecondOrderSource('BRIC40_1min.csv', symbols, lineProcessor)

print "Ready"
class MyObserver(PricesListener):
    def update(self, ss, when):
        strLine = Long.toString(when.getTimeInMillis()).encode('utf-8')
        strLine = strLine + when.toString().encode('utf-8')
        for s in symbols:
            if s in ss:
                strLine = strLine + ',' \
                    + str(market.getLastPrice(0,s+'-OPEN')) + ','\
                    + str(market.getLastPrice(0,s+'-HIGH')) + ','\
                    + str(market.getLastPrice(0,s+'-LOW')) + ','\
                    + str(market.getLastPrice(0,s+'-CLOSE')) + ','\
                    + str(market.getLastPrice(0,s+'-VOLUME')) + ','\

            else:
                strLine = strLine + ',-,-,-,-,-'
        
        print strLine
                


market = RandomAccessMarket(0.0, 5000)
lineProcessor.addMarketListener(market)
lineProcessor.addPricesListener(MyObserver())

print "Go!"

strLine = 'milliseconds'
for s in symbols:
    strLine = strLine + ',' + s + '-OPEN'
    strLine = strLine + ',' + s + '-HIGH'
    strLine = strLine + ',' + s + '-LOW'
    strLine = strLine + ',' + s + '-CLOSE'
    strLine = strLine + ',' + s + '-Volume'
print strLine

source.run()
