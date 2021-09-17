# Structure contstructor value types

Checks Structure constructor string literal that each key has typed value.

## Noncompliant Code Example

Structure keys initalizing without any default value that set empty types for the structure key for lifetime.

```bsl
// @strict-types

Procedure Test() Export
	
	Params = new Structure("Key1, Key2, Key3");
	// some code...
	
	Params.Key1 = 1345;
	Params.Key2 = "New vlaue";
	Params.Key3 = Catalogs.Products.Service;
	
EndProcedure
```

## Compliant Solution

Initialize structure keys with default typed values in the constructor or insert new key with default typed value.

```bsl
// @strict-types

Procedure Test() Export
	
	Params = new Structure("Key1, Key2", 0, "");
	Params.Insert("Key3", Catalogs.Products.EmptyRef());
	// some code...
	
	Params.Key1 = 1345;
	Params.Key2 = "New vlaue";
	Params.Key3 = Catalogs.Products.Service;
	
EndProcedure
```

## See

