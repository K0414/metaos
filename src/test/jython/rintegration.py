#
# Predictor using R defined classes over BRIC40.
#

# TO DO: get symbols from file source
symbols = [ '1288.HK', '3988.HK', '0883.HK', '0939.HK', '2628.HK', '3968.HK', '0941.HK', '0688.HK', '0386.HK', '1088.HK', '0728.HK', '0762.HK', '1398.HK', '0857.HK', '2318.HK', '0700.HK', 'GAZPq.L', 'LKOHyq.L', 'NKELyq.L', 'NVTKq.L', 'RELIq.L', 'ROSNq.L', 'SNGSyq.L', 'TATNxq.L', 'BSBR.N', 'BBD.N', 'ABV.N', 'CIG.N', 'SID.N', 'GGB.N', 'HDB.N', 'IBN.N', 'ITUB.N', 'MBT.N', 'PBR.N', 'TNE.N', 'VALE.N', 'VIP.N', 'BIDU.OQ', 'INFY.OQ']

symbol1 = symbols[16]
symbol2 = symbols[0]

source = CSVUnorderedData.getInstance().reuters('BRIC40_1min.csv', symbols)

# R code: create predictor object
interpreteR = R([args[0]])
interpreteR.eval("predictor <- lsPredictor()")

# Bind R predictor to source events through an observer
class MyObserver(MarketObserver):
    def __init__(self):
        self.anyCoincidence = False;

    def update(self, ss, when):
        if symbol1 in ss and symbol2 in ss:
            self.anyCoincidence = True
            interpreteR.eval('predictor$learn(' \
                + str(market.getLastPrice(0, symbol1 + '-CLOSE')) + ','
                + str(market.getLastPrice(0, symbol2 + '-CLOSE')) + ')')

            strLine = Long.toString(when.getTimeInMillis()).encode('utf-8')
            strLine = strLine + ',' \
                    + Double.toString(market.getLastPrice(0,symbol1 + '-OPEN'))\
                            .encode('utf-8') + ','\
                    + Double.toString(market.getLastPrice(0,symbol1 + '-HIGH'))\
                            .encode('utf-8') + ',' \
                    + Double.toString(market.getLastPrice(0,symbol1 + '-LOW'))\
                            .encode('utf-8') + ','\
                    + Double.toString(market.getLastPrice(0,symbol1+'-CLOSE'))\
                            .encode('utf-8') + ','\
                    + Long.toString(market.getLastVolume(0,symbol1))\
                            .encode('utf-8')
            strLine = strLine + ',' \
                    + Double.toString(market.getLastPrice(0,symbol2 + '-OPEN'))\
                            .encode('utf-8') + ','\
                    + Double.toString(market.getLastPrice(0,symbol2 + '-HIGH'))\
                            .encode('utf-8') + ',' \
                    + Double.toString(market.getLastPrice(0,symbol2 + '-LOW'))\
                            .encode('utf-8') + ','\
                    + Double.toString(market.getLastPrice(0,symbol2+'-CLOSE'))\
                            .encode('utf-8') + ','\
                    + Long.toString(market.getLastVolume(0,symbol2))\
                            .encode('utf-8')
            print strLine

        if symbol1 in ss and not symbol2 in ss and self.anyCoincidence:
            y = interpreteR.evalDouble('predictor$predict(' \
                + str(market.getLastPrice(0, symbol1 + '-CLOSE')) + ')')

            strLine = Long.toString(when.getTimeInMillis()).encode('utf-8')
            strLine = strLine + ',' \
                    + Double.toString(market.getLastPrice(0,symbol1 + '-OPEN'))\
                            .encode('utf-8') + ','\
                    + Double.toString(market.getLastPrice(0,symbol1 + '-HIGH'))\
                            .encode('utf-8') + ',' \
                    + Double.toString(market.getLastPrice(0,symbol1 + '-LOW'))\
                            .encode('utf-8') + ','\
                    + Double.toString(market.getLastPrice(0,symbol1+'-CLOSE'))\
                            .encode('utf-8') + ','\
                    + Long.toString(market.getLastVolume(0,symbol1))\
                            .encode('utf-8')
            strLine = strLine + ',-,-,-,' + str(y) + ',-'
            print strLine


# Join everything together
market = SequentialAccessMarket(0.0, 5000)
source.addMarketListener(market)
source.addListener(MyObserver())

# Ready, steady, go
print 'milliseconds,' + symbol1 + '-OPEN,' + symbol1 + '-HIGH,' \
    + symbol1 + '-LOW,' + symbol1 + '-CLOSE,' + symbol1 + '-VOLUME,' \
    + symbol2 + '-OPEN,' + symbol2 + '-HIGH,' \
    + symbol2 + '-LOW,' + symbol2 + '-CLOSE,' + symbol2 + '-VOLUME'

source.run()

# Land down
interpreteR.end()
