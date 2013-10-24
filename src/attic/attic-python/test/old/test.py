oct012010 = CalUtils.createStrikeDate(01, 10, 2010)
oct032010 = CalUtils.createStrikeDate(03, 10, 2010)

p1 = [1.0, 2.0, 3.0]
p2 = [4.0, 5.0, 6.0]
p3 = [7.0, 7.0, 8.0]
v1 = SingleVolatility("A", oct012010, oct032010, p1)
v2 = SingleVolatility("B", oct012010, oct032010, p2)
v3 = SingleVolatility("C", oct012010, oct032010, p3)

print v1.getVolatility(),v2.getVolatility(),v3.getVolatility()

weights = [0.3, 0.4, 0.3]
sigmas = [v1, v2, v3]
pv = PortfolioVolatility(sigmas, weights, oct012010, oct032010)

print pv.getVolatility()
