from com.luisfcanals.techan.data import *

repository = YahooHistorical(2010)

# Gets infor for:
#   SANTANDER SAN.MC
#   IBERDROLA IBR.MC
#   ACCIONA ANA.MC
#   TECNICAS REUNIDAS TRE.MC
#   EBRO FOODS EBRO.MC
#   ABERTIS ABE.MC
#   QQQQ POWERSHARES QQQQ
#   ACCION EUROSTOXX 50 BBVAE.MC
#   LYXOR ETF EUROSTOXX 50 MSE.MC
#   LYXOR ETF EUROSTOXX 50 DAILY AVERAGE LEV.MC
#   ACCION FTSE LATIBEX TOP ETF BBVAL.MC
#   ACCION FSE LATIBEX BBVA ETF BBVAB.MC
#   RYDEX CURRENCYCSHARES SWIS FRAN FXF

underlyings = [ 'SAN.MC', 'IBR.MC', 'ANA.MC', 'TRE.MC', 'EBRO.MC', \
                'ABE.MC', 'QQQQ', 'FXF' ]

sept1 = CalUtils.createStrikeDate(1, 9, 2010)
today = Calendar.getInstance()
CalUtils.normalizeCalendar(today)

for u in underlyings:
    day = CalUtils.clone(sept1)
    openPrices = []
    closePrices = []
    while day.before(today):
        p = repository.getPosition(u, day)
        day.add(Calendar.DAY_OF_MONTH, 1)
        if p!=None: 
            closePrices.append(p.close)
            openPrices.append(p.open)
    
    print u,
    for x in openPrices:
        print x,
    print
    print u,
    for x in closePrices:
        print x,
    print
