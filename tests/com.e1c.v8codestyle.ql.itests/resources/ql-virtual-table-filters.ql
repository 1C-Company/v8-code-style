SELECT
	StocksBalanceAndTurnovers.Product,
	StocksBalanceAndTurnovers.Warehouse,
	StocksBalanceAndTurnovers.QuntityExpense,
	StocksBalanceAndTurnovers.AmountExpense,
	StocksBalanceAndTurnovers.AmountTurnover
FROM
	AccumulationRegister.Stocks.BalanceAndTurnovers AS StocksBalanceAndTurnovers
WHERE
	StocksBalanceAndTurnovers.Warehouse <> ""
	AND StocksBalanceAndTurnovers.Product.ProductType = VALUE(Enum.ProductType.Service)
	AND StocksBalanceAndTurnovers.AmountTurnover > 100
