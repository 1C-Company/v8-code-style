SELECT
	StocksBalanceAndTurnovers.Product,
	StocksBalanceAndTurnovers.Warehouse,
	StocksBalanceAndTurnovers.QuntityExpense,
	StocksBalanceAndTurnovers.AmountExpense,
	StocksBalanceAndTurnovers.AmountTurnover
FROM
	AccumulationRegister.Stocks.BalanceAndTurnovers(,,,, Warehouse <> ""
	AND Product.ProductType = VALUE(Enum.ProductType.Service)) AS StocksBalanceAndTurnovers
WHERE
	StocksBalanceAndTurnovers.AmountTurnover > 100
