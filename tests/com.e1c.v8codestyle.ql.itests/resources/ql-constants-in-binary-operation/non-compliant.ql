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
    