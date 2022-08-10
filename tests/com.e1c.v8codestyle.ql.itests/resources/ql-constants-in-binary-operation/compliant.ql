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
    