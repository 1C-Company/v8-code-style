# Using binary operations with constants in queries.

Using binary operations with constants in queries is forbidden.

## Noncompliant Code Example

```bsl
SELECT
    Products.Name AS Name,
    "My" + "Goods" AS Code
FROM
    Catalogs.Products AS Products;

SELECT 
    Products.Name AS Name,
    FieldName AS Code
FROM
    Catalogs.Products AS Products
WHERE 
    FieldName LIKE "123" + "%";
```

## Compliant Solution

```bsl
SELECT
    Products.Name AS Name,
    "MyGoods" AS Code
FROM
    Catalogs.Products AS Products;

SELECT 
    Products.Name AS Name,
    FieldName AS Code
FROM
    Catalogs.Products AS Products
WHERE 
    FieldName LIKE "123%";
```

## See

- [Effective query conditions](https://support.1ci.com/hc/en-us/articles/360011121019-Effective-query-conditions)
- [Specifics of using LIKE operator in queries](https://support.1ci.com/hc/en-us/articles/360011001500-Specifics-of-using-LIKE-operator-in-queries)