# Structure key modification outside constructor function

If Structure was created by constructor function then changing the composition of the structure keys using methods
`Structure.Insert("Key", ...); Structure.Delete("Key"); Structure.Clear();` 
can lead to errors due to the uncertainty of data composition.


## Noncompliant Code Example

Should not clear all structure keys, delete key or replace key of external structure.

```bsl
// @strict-types

Procedure Incorrect1() Export

	TestStucture = Constructor();
	TestStucture.Clear(); 
	TestStucture.Insert("Key1", 10); 
	TestStucture.Delete("Key2"); 

EndProcedure

// Returns:
//  Structure:
// * Key1 - Boolean - 
// * Key2 - Number - 
Function Constructor() Export

	TestStucture = new Structure("Key1, Key2", true, 10);
	TestStucture.Insert("Key2", 20);

	Return TestStucture; 

EndFunction
```

## Compliant Solution

Access to existing key directly and set new value. 
Instead of delete key should set a value that represents blank or initial value.

```bsl
// @strict-types

Procedure Correct1() Export

	TestStucture = Constructor();
	TestStucture.Key1 = false; 
	TestStucture.Key2 = -1; 

EndProcedure

// Returns:
//  Structure:
// * Key1 - Boolean - 
// * Key2 - Number - 
Function Constructor() Export

	TestStucture = new Structure("Key1, Key2", true, 10);
	TestStucture.Insert("Key2", 10);

	Return TestStucture; 

EndFunction
```

## See

