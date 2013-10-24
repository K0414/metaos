
# Maybe here it would be better to use a DifferentiableMultivariateFunction
class FixedProfitFunction(MultivariateRealFunction) :
  sigmas = None
  pv = None
  desiredProfit = None
  profit = None

  def __init__(self, desiredProfit):
    oct012010 = CalUtils.createStrikeDate(01, 10, 2010)
    oct032010 = CalUtils.createStrikeDate(03, 10, 2010)

    r = [0.2, 0.4, 0.1]  # Calculable a partir de la evolucion de los p1, p2, p3
    p1 = [1.0, 2.0, 3.0]
    p2 = [4.0, 5.0, 6.0]
    p3 = [7.0, 7.0, 8.0]
    v1 = SingleVolatility("A", oct012010, oct032010, p1)
    v2 = SingleVolatility("B", oct012010, oct032010, p2)
    v3 = SingleVolatility("C", oct012010, oct032010, p3)

    self.sigmas = [v1, v2, v3]
    self.pv = PortfolioVolatility(self.sigmas, None, oct012010, oct032010)
    self.desiredProfit = desiredProfit
    self.profit = r

  def everyWeightPositive(self, weights):
    x = 0
    for w in weights:
        if w<0:
            x = x - w
    return x * 10


  def weightsSumOne(self, weights):
    x = 0
    for w in weights:
        x = x + w
    return abs(1-x) * 100


  def fixToDesiredProfit(self, weights):
    x = 0
    i = 0
    for w in weights:
        x = x + w * self.profit[i]
        i = i + 1
    return abs(self.desiredProfit - x) * 10


  #
  # Interface implementation
  #
  def value(self, weights):
    penalty = self.everyWeightPositive(weights) \
        + self.weightsSumOne(weights) + self.fixToDesiredProfit(weights)

    self.pv.setWeights(weights)
    return self.pv.getVolatility() + penalty

    




class FixedVolatilityFunction(MultivariateRealFunction) :
  sigmas = None
  pv = None
  desiredProfit = None
  profit = None

  def __init__(self, desiredVolatility):
    oct012010 = CalUtils.createStrikeDate(01, 10, 2010)
    oct032010 = CalUtils.createStrikeDate(03, 10, 2010)

    r = [0.2, 0.4, 0.1]  # Calculable a partir de la evolucion de los p1, p2, p3
    p1 = [1.0, 2.0, 3.0]
    p2 = [4.0, 5.0, 6.0]
    p3 = [7.0, 7.0, 8.0]
    v1 = SingleVolatility("A", oct012010, oct032010, p1)
    v2 = SingleVolatility("B", oct012010, oct032010, p2)
    v3 = SingleVolatility("C", oct012010, oct032010, p3)

    self.sigmas = [v1, v2, v3]
    self.pv = PortfolioVolatility(self.sigmas, None, oct012010, oct032010)
    self.desiredVolatility = desiredVolatility
    self.profit = r

  def everyWeightPositive(self, weights):
    x = 0
    for w in weights:
        if w<0:
            x = x - w
    return x * 10


  def weightsSumOne(self, weights):
    x = 0
    for w in weights:
        x = x + w
    return abs(1-x) * 100


  def fixToDesiredVolatility(self, weights):
    self.pv.setWeights(weights)
    return abs(self.pv.getVolatility() - self.desiredVolatility) * 10

  #
  # Interface implementation
  #
  def value(self, weights):
    penalty = self.everyWeightPositive(weights) \
        + self.weightsSumOne(weights) + self.fixToDesiredVolatility(weights)

    x = 0
    i = 0
    for w in weights:
        x = x + w * self.profit[i]
        i = i + 1
    
    return x - penalty



 


    
optimizer = NelderMead()

targetFunction = FixedVolatilityFunction(0.839040782962)
w = optimizer.optimize(targetFunction, GoalType.MAXIMIZE, [0.2, 0.6, 0.2])
print w.getPoint(), w.getValue()

targetFunction = FixedProfitFunction(0.3)
w = optimizer.optimize(targetFunction, GoalType.MINIMIZE, [0.2, 0.6, 0.2])
print w.getPoint(), w.getValue()



