# The query is missing a NULL check for a field that could potentially contain NULL.

2. When you order a query result by fields that might contain NULL, take into account that the sorting priority of NULL varies in different DBMS.

## Noncompliant Code Example

```bsl
SELECT
  CatalogProducts.Ref AS ProductRef,
  InventoryBalance.QuantityBalance AS QuantityBalance
FROM
  Catalog.Products AS CatalogProducts
    LEFT JOIN AccumulationRegister.Inventory.Balance AS InventoryBalance
    BY (InventoryBalance.Products = CatalogProducts.Ref)

ORDER BY
  QuantityBalance
```

## Compliant Solution

```bsl
SELECT
  CatalogProducts.Ref AS ProductRef,
  ISNULL(InventoryBalance.QuantityBalance, 0) AS QuantityBalance
FROM
  Catalog.Products AS CatalogProducts
    LEFT JOIN AccumulationRegister.Inventory.Balance AS InventoryBalance
    BY (InventoryBalance.Products = CatalogProducts.Ref)

ORDER BY
  QuantityBalance;
  
SELECT
  CatalogProducts.Ref AS ProductRef,
  CASE WHEN InventoryBalance.QuantityBalance IS NOT NULL THEN 0 ELSE InventoryBalance.QuantityBalance AS QuantityBalance
FROM
  Catalog.Products AS CatalogProducts
    LEFT JOIN AccumulationRegister.Inventory.Balance AS InventoryBalance
    BY (InventoryBalance.Products = CatalogProducts.Ref)

ORDER BY
  QuantityBalance;
```

## See

- [Ordering query results](https://support.1ci.com/hc/en-us/articles/360011120859-Ordering-query-results)
- [Using the CASE operation](https://support.1ci.com/hc/en-us/articles/360007870794-Query-language#using_the_isnull___function)
- [Appendix 8. Features of operating with different DBMS](https://support.1ci.com/hc/en-us/articles/6347699838098-8-3-IBM-Db2)