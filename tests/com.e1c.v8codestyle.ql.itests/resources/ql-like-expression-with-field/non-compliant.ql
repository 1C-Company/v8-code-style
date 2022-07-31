SELECT
    StocksBalanceAndTurnovers.Warehouse
FROM
    AccumulationRegister.Stocks.BalanceAndTurnovers AS StocksBalanceAndTurnovers
WHERE
    StocksBalanceAndTurnovers.Warehouse LIKE Table.Field
;

SELECT
    StocksBalanceAndTurnovers.Warehouse
FROM
    AccumulationRegister.Stocks.BalanceAndTurnovers AS StocksBalanceAndTurnovers
WHERE
    StocksBalanceAndTurnovers.Warehouse LIKE Table.Field + "%Literal"
;

SELECT
    StocksBalanceAndTurnovers.Warehouse
FROM
    AccumulationRegister.Stocks.BalanceAndTurnovers AS StocksBalanceAndTurnovers
WHERE
    StocksBalanceAndTurnovers.Warehouse LIKE Table.Field ESCAPE SpecialCharacter
;

SELECT
    StocksBalanceAndTurnovers.Warehouse
FROM
    AccumulationRegister.Stocks.BalanceAndTurnovers AS StocksBalanceAndTurnovers
WHERE
    StocksBalanceAndTurnovers.Warehouse LIKE Table.Field1 + Table.Field2
;

SELECT
    StocksBalanceAndTurnovers.Warehouse
FROM
    AccumulationRegister.Stocks.BalanceAndTurnovers AS StocksBalanceAndTurnovers
WHERE
    StocksBalanceAndTurnovers.Warehouse LIKE Table.Field + &Parameter
;

SELECT
    StocksBalanceAndTurnovers.Warehouse
FROM
    AccumulationRegister.Stocks.BalanceAndTurnovers AS StocksBalanceAndTurnovers
WHERE
    StocksBalanceAndTurnovers.Warehouse LIKE Warehouse
;
