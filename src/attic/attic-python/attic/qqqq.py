from com.luisfcanals.techan.data import *

repository = YahooHistorical(2010)

# Gets info:
#   QQQQ POWERSHARES QQQQ

underlyings = [ 'QQQQ' ]

sept1 = CalUtils.createStrikeDate(1, 9, 2010)
today = Calendar.getInstance()
CalUtils.normalizeCalendar(today)

for u in underlyings:
    day = CalUtils.clone(sept1)
    openPrices = []
    closePrices = []
    highs = []
    lows = []
    while day.before(today):
        p = repository.getPosition(u, day)
        day.add(Calendar.DAY_OF_MONTH, 1)
        if p!=None: 
            closePrices.append(p.close)
            openPrices.append(p.open)
            highs.append(p.high);
            lows.append(p.low);

    print u, 'Open',
    for x in openPrices:
        print x,
    print
    print u, 'Close',
    for x in closePrices:
        print x,
    print
    print u, 'Max',
    for x in highs:
        print x,
    print
    print u, 'Min',
    for x in lows:
        print x,
    print
