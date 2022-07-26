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
    StocksBalanceAndTurnovers.Warehouse LIKE Table.Field1 + Table.Field2
;

SELECT
    StocksBalanceAndTurnovers.Warehouse
FROM
    AccumulationRegister.Stocks.BalanceAndTurnovers AS StocksBalanceAndTurnovers
WHERE
    StocksBalanceAndTurnovers.Warehouse LIKE Table.Field1 + &Parameter
;

ВЫБРАТЬ
    Товары.Ссылка
ИЗ
    Справочник.Товары КАК Товары
ГДЕ
    Товары.СтранаПроисхождения.Наименование ПОДОБНО Таблица.Поле
;

ВЫБРАТЬ
    Товары.Ссылка
ИЗ
    Справочник.Товары КАК Товары
ГДЕ
    Товары.СтранаПроисхождения.Наименование ПОДОБНО Таблица.Поле1 + "%Литерал"
;

ВЫБРАТЬ
    Товары.Ссылка
ИЗ
    Справочник.Товары КАК Товары
ГДЕ
    Товары.СтранаПроисхождения.Наименование ПОДОБНО Таблица.Поле1 + Таблица.Поле2
;

ВЫБРАТЬ
    Товары.Ссылка
ИЗ
    Справочник.Товары КАК Товары
ГДЕ
    Товары.СтранаПроисхождения.Наименование ПОДОБНО Таблица.Поле1 + &Параметр
;
