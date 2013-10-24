#
# Looks for coincidences in symbols with different ending.
# 

symbols = [ '1288.HK', '3988.HK', '0883.HK', '0939.HK', '2628.HK', '3968.HK', '0941.HK', '0688.HK', '0386.HK', '1088.HK', '0728.HK', '0762.HK', '1398.HK', '0857.HK', '2318.HK', '0700.HK', 'GAZPq.L', 'LKOHyq.L', 'NKELyq.L', 'NVTKq.L', 'RELIq.L', 'ROSNq.L', 'SNGSyq.L', 'TATNxq.L', 'BSBR.N', 'BBD.N', 'ABV.N', 'CIG.N', 'SID.N', 'GGB.N', 'HDB.N', 'IBN.N', 'ITUB.N', 'MBT.N', 'PBR.N', 'TNE.N', 'VALE.N', 'VIP.N', 'BIDU.OQ', 'INFY.OQ']

source = CSVUnorderedData.getInstance().reuters('BRIC40_1min.csv', symbols)

class MyObserver(MarketObserver):
    def update(self, ss, when):
        coincidences = []
        for s in ss:
            coincidences.append(s[s.find("."):])

        coincidences = set(coincidences)
        if len(coincidences)>1:
            strLine = Long.toString(when.getTimeInMillis()).encode('utf-8')+' '
            for s in coincidences: strLine = strLine + ' ' + s
            strLine = strLine + ' '
            for s in ss: strLine = strLine + ',' + s
            print strLine.encode('utf-8') 
                


market = RandomAccessMarket(0.0, 5000)
source.addMarketListener(market)
source.addListener(MyObserver())

source.run()
