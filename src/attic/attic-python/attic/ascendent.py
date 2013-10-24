nov29 = CalUtils.createStrikeDate(29, 11, 2010)

portfolio = Portfolio()
market = Market()
# EURCHF - how many CHF to buy 1 EUR

# Prospects: min 1.3308 max 1.3498, actual 1.3435.
# We want to cover against volatility.
op1 = EuropeanShortPut(0.0084, 1.3380, nov29, "EURCHF", 200000)
op2 = EuropeanLongCall(0.0144, 1.3400, nov29, "EURCHF", 1300)
# CHFEUR - how many EUR to buy 1 CHF (Loan is inverse for me)
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
