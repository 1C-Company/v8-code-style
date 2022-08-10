# Using binary operations with constants or parameters in queries.

Do not generate a template string using calculation or use string concatenation with the query language.

This requirement is based on some specifics of migrating applications to various database management systems.

## Noncompliant Code Example

```bsl
SELECT
    Products.Name AS Name,
    "My" + "Goods" AS Code
FROM
    Catalogs.Products AS Products;

SELECT
    Products.Name AS Name,
    "My" + &Parameter AS Code
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
    &Parameter AS Code
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