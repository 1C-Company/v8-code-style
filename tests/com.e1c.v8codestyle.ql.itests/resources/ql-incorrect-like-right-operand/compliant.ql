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
    StocksBalanceAndTurnovers.Warehouse LIKE "%Literal" + "_Stirng"
;

SELECT
    StocksBalanceAndTurnovers.Warehouse
FROM
    AccumulationRegister.Stocks.BalanceAndTurnovers AS StocksBalanceAndTurnovers
WHERE
    StocksBalanceAndTurnovers.Warehouse LIKE "%Literal" + &Parameter
;

ВЫБРАТЬ
    Товары.Ссылка
ИЗ
    Справочник.Товары КАК Товары
ГДЕ
    Товары.СтранаПроисхождения.Наименование ПОДОБНО "%Литерал"
;

ВЫБРАТЬ
    Товары.Ссылка
ИЗ
    Справочник.Товары КАК Товары
ГДЕ
    Товары.СтранаПроисхождения.Наименование ПОДОБНО "%Литерал" + "_Строка"
;

ВЫБРАТЬ
    Товары.Ссылка
ИЗ
    Справочник.Товары КАК Товары
ГДЕ
    Товары.СтранаПроисхождения.Наименование ПОДОБНО "%Литерал" + &Параметр
;
