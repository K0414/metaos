nov29 = CalUtils.createStrikeDate(29, 11, 2010)

portfolio = Portfolio()
market = Market()
# EURCHF - how many CHF to buy 1 EUR
op1 = EuropeanLongCall(0.01440, 1.3335, nov29, "EURCHF", 20000)
op2 = EuropeanShortCall(0.00220, 1.3615, nov29, "EURCHF", 200000)
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
