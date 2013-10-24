
#
# Defines market evolution observer.
# In this case: remember only 1288.HK and XAG moments.
#
x = []
y = []
class MyObserver(MarketObserver):
    def update(self, ss, when):
        strLine = Long.toString(when.getTimeInMillis()).encode('utf-8')
        if 'XAG'  in ss and '1288.HK' in ss:
            x.append(market.getLastPrice(0,'XAG-CLOSE'))
            y.append(market.getLastPrice(0,'1288.HK-CLOSE'))

                
#
# Creates two sources of data.
#
bricSymbols = [ '1288.HK',  '3988.HK', '0883.HK', '0939.HK', '2628.HK', '3968.HK', '0941.HK', '0688.HK', '0386.HK', '1088.HK', '0728.HK', '0762.HK', '1398.HK', '0857.HK', '2318.HK', '0700.HK', 'GAZPq.L', 'LKOHyq.L', 'NKELyq.L', 'NVTKq.L', 'RELIq.L', 'ROSNq.L', 'SNGSyq.L', 'TATNxq.L', 'BSBR.N', 'BBD.N', 'ABV.N', 'CIG.N', 'SID.N', 'GGB.N', 'HDB.N', 'IBN.N', 'ITUB.N', 'MBT.N', 'PBR.N', 'TNE.N', 'VALE.N', 'VIP.N', 'BIDU.OQ', 'INFY.OQ']
bricSource = CSVUnorderedData.getInstance().reuters('BRIC40_1min.csv', \
    bricSymbols)

xagSource = CSVGeneral.getInstance().simpleContinuousSingleSource('XAG', \
    'XAGUSD1.csv', 'yyyy.MM.dd,HH:mm', \
    '([0-9]{4}.[0-9]{2}.[0-9]{2},[0-9]{2}:[0-9]{2}),(.*),(.*),(.*),(.*),(.*)', \
    [Fields.DATE,Fields.OPEN,Fields.HIGH,Fields.LOW,Fields.CLOSE,Fields.VOLUME])


#
# Putting all together!
#
source = CompossedSource([bricSource, xagSource])
market = RandomAccessMarket(0.0, 5000)
source.addMarketListener(market)
source.addListener(MyObserver())

# Go and play
source.run()

# Show results: the set of pairs XAG - 1288.HK when 
i=0
fd = open('output.csv', 'a+')
while i<len(x):
    str = Double.toString(x[i]).encode('utf-8') + ',' \
        + Double.toString(y[i]).encode('utf-8')
    print str
    fd.write(str + '\n')
    i = i + 1

fd.close()
