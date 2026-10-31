# Structures and Value Tables as Procedure and Function Parameters

The public API must include a constructor function that returns either a template structure with predefined properties or an empty value table (value tree) with typed columns.
The validation check inspects the return type of the function; for a constructor function, it must be either Structure, ValueTable, or ValueTree.

## Incorrect

```bsl
#Region Public

// Constructor function is missing

#EndRegion
```

## Correct

```bsl
#Region Public

Function LinePriceFillingParameters() Export
 
 PriceFillingParameters = New Structure;
 PriceFillingParameters.Insert("Date", SessionCurrentDate());
 PriceFillingParameters.Insert("Currency", MainCurrency());
 PriceFillingParameters.Insert("RecalculateAmount", True);
 Return PriceFillingParameters;
 
EndFunction

#EndRegion
```

## See also

[Structures and value tables as parameters of procedures and functions](https://its.1c.ru/db/v8std#content:641:hdoc)