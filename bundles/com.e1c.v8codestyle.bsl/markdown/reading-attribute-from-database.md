# Reading single object attribute from the database

When you read separate object attributes from the database, note that once you call the GetObject method or
access object attributes separated by period from a reference, the whole object is imported from the database with its tabular sections.

That is why to read separate attribute values from the database, use queries. 

## Noncompliant Code Example

```bsl

Procedure FillCountryCodeAndDescription()
 
 CountryRef = … // Getting a reference to a catalog item
 CountryCode = CountryRef.Code; // The first query imports the whole object
 CountryDescription = CountryRef.Description;
 
EndProcedure

```

## Compliant Solution

```bsl

Procedure FillCountryCodeAndDescription()
 
 Query = New Query(
  "SELECT
  | WorldCountries.Code,
  | WorldCountries.Description
  |FROM
  | Catalog.WorldCountries AS WorldCountries
  |WHERE
  | WorldCountries.Ref = &Ref");
 Query.SetParameter("Ref", Ref);
 
 Selection = Query.Execute().Select();
 Selection.Next();

 CountryCode = Selection.Code;
 CountryDescription = Selection.Description;

EndProcedure

```

To simplify the syntax, use special functions ObjectAttributesValues or ObjectAttributeValue. These functions belong to Standard Subsystems Library.
In this case, the example will be as follows:

```bsl
Procedure FillCountryCodeAndDescription()

 AttributesValues = Common.ObjectAttributesValues(CountryRef, "Code, Description");
 CountryCode = AttributesValues.Code;
 CountryDescription = AttributesValues.Description;
 
EndProcedure
```

## See


- [Reading specific object attributes from the database](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/Data_processing/Data_processing_and_modification/Reading_specific_object_attributes_from_the_database)
