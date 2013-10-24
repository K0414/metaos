nov29 = CalUtils.createStrikeDate(29, 11, 2010)

portfolio = Portfolio()
market = Market()
# EURCHF - how many CHF to buy 1 EUR

# Prospects: min 1.3308 max 1.3498, actual 1.3435.
# We want to cover against volatility
op1 = EuropeanLongPut(0.0134, 1.3430, nov29, "EURCHF", 10000)
op2 = EuropeanShortCall(0.0026, 1.3645, nov29, "EURCHF", 200000)
# CHFEUR - how many EUR to buy 1 CHF 
debt = Loan(nov29, "CHFEUR", 1300)

portfolio.add(op1)
portfolio.add(op2)
portfolio.add(debt)


# Simulation
print "EURCHF", ",", "Payoff", ",", "Loan"
for a in xrange((1.3800 - 1.3280) / 0.0001):
  x = a * 0.0001 + 1.3280;
  market.setPrice(nov29, "EURCHF", x)
  market.setPrice(nov29, "CHFEUR", 1/x)
  portfolio.setMarket(market)
  print x, ",", portfolio.getProfit(nov29) - portfolio.totalCost(), ",", debt.getProfit(nov29)
