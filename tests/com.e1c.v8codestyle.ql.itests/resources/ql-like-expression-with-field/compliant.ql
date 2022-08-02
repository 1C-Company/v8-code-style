SELECT
	StocksBalanceAndTurnovers.Warehouse
FROM
	AccumulationRegister.Stocks.BalanceAndTurnovers AS StocksBalanceAndTurnovers
WHERE
	StocksBalanceAndTurnovers.Warehouse LIKE "%Literal"
;

SELECT
    StocksBalanceAndTurnovers.Warehouse
FROM
    AccumulationRegister.Stocks.BalanceAndTurnovers AS StocksBalanceAndTurnovers
WHERE
    StocksBalanceAndTurnovers.Warehouse LIKE "%Literal!%" ESCAPE "!"
;

SELECT
    StocksBalanceAndTurnovers.Warehouse
FROM
    AccumulationRegister.Stocks.BalanceAndTurnovers AS StocksBalanceAndTurnovers
WHERE
    StocksBalanceAndTurnovers.Warehouse LIKE "%Literal" + "_Stirng"
;

SELECT
    StocksBalanceAndTurnovers.Warehouse
FROM
    AccumulationRegister.Stocks.BalanceAndTurnovers AS StocksBalanceAndTurnovers
WHERE
    StocksBalanceAndTurnovers.Warehouse LIKE "%Literal" + &Parameter
;
