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

