
symbols = [ 'ABE.MC','ABG.MC','ACS.MC','ACX.MC','ISPA.AS','TEF.MC','TL5.MC',\
            'TRE.MC' ]

#lineProcessor = CSVReutersAdaptative('BRIC_1min.csv')
textFormat = MessageFormat("{0}")
dateFormat = SimpleDateFormat('dd-MMM-yyyy')
timeFormat = SimpleDateFormat('HH:mm:ss.SSS')
doubleFormat = DecimalFormat('#.##')

lineProcessor = CSVSourceLineProcessor([
    textFormat,dateFormat,timeFormat,None,textFormat,doubleFormat,doubleFormat,\
    doubleFormat,doubleFormat,doubleFormat,doubleFormat,doubleFormat,None,\
    doubleFormat,doubleFormat,doubleFormat,doubleFormat,doubleFormat,\
    doubleFormat,doubleFormat,doubleFormat],\
    [None,None,None,None,None,CLOSE(PRICE),VOLUME(PRICE),\
     EXTENDED(PRICE,"VWAP"),CLOSE(BID),VOLUME(BID),CLOSE(ASK),VOLUME(ASK),\
     None,EXTENDED(PRICE,"New Price"),EXTENDED(PRICE,"New Vol"),\
     EXTENDED(PRICE,"30 Day ATM IV Call"),EXTENDED(PRICE,"60 Day ATM IV Call"),\
     EXTENDED(PRICE,"90 Day ATM IV Call"),EXTENDED(PRICE,"30 Day ATM IV Put"),\
     EXTENDED(PRICE,"60 Day ATM IV Put"),EXTENDED(PRICE,"90 Day ATM IV Put")],
     0,[1,2])
source = SecondOrderSource('s.alvarez.telena\@grupobbva.com--N25010774-part001.csv', symbols, lineProcessor)

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
