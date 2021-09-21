# Variable has value type

Check of module strict types system that every variable has value type

## Noncompliant Code Example

Declared local variable in method:

```bsl
// @strict-types

Procedure Test() Export
	Var TestVar;
	
	// code here
EndProcedure
```

Declared variable in object module and hidden initialization in some method:

```bsl
// @strict-types

Var ObjectParameters;

Procedure BeforeWrite(Cancel)
	
	If DataExchange.Load Then
		Return;
	EndIf;
	
	ObjectParameters = NewObjectParameters();
	
	If ValueIsFilled(ObjectParameters.Param1) Then
		// code here
	EndIf;
	
EndProcedure

// Returns:
//  Structure:
// * Param1 - String -
Function NewObjectParameters()
	
	Params = New Structure();
	Params.Insert("Param1", "String value");
	
	return Params;
	
EndFunction
```

## Compliant Solution

Initialize local variable with default empty value:

```bsl
// @strict-types

Procedure Test() Export
	TestVar = 0;
	
	// code here
EndProcedure
```

Initialize module variable with default value in module statements and add typed in-line documentation comments for variable declaration.

```bsl
// @strict-types

Var ObjectParameters; // see NewObjectParameters


Procedure BeforeWrite(Cancel)
	
	If DataExchange.Load Then
		Return;
	EndIf;
	
	If ValueIsFilled(ObjectParameters.Param1) Then
		// code here
	EndIf;
	
EndProcedure


// Returns:
//  Structure:
// * Param1 - String -
Function NewObjectParameters()
	
	Params = New Structure();
	Params.Insert("Param1", "String value");
	
	Return Params;
	
EndFunction

// Explicitly initialize value in module statements
ObjectParameters = NewObjectParameters();
```

## See

