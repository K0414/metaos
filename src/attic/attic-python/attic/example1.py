nov29 = CalUtils.createStrikeDate(29, 11, 2010)

portfolio = Portfolio()
market = Market()
# EURCHF - how many CHF to buy 1 EUR
op1 = EuropeanLongCall(0.001, 1.3585, nov29, "EURCHF", 1300)
op2 = EuropeanLongPut(0.001, 1.3585, nov29, "EURCHF", 1300)
op3 = EuropeaShortPut(0.001, 1.3585, nov29, "EURCHF", 1300)
op4 = EuropeanShortCall(0.001, 1.3585, nov29, "EURCHF", 1300)
# CHFEUR - how many EUR to buy 1 CHF 
debt = Loan(nov29, "CHFEUR", 1300)

portfolio.add(op1)
portfolio.add(debt)


# Simulation
market.setPrice(nov29, "EURCHF", 1.3689)
market.setPrice(nov29, "CHFEUR", 1/1.3689)
portfolio.setMarket(market)
print portfolio.getProfit(nov29) - portfolio.totalCost()
