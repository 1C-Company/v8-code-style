# Documentation comment field use declaration of complex type instead of link to type

## Noncompliant Code Example

```bsl
// Parameters:
//  Parameters - Structure - (See NewStructureObject.)
Procedure NonComplaint(Parameters) Export
	// empty
EndProcedure

// Returns:
//  Parameters - Structure:
//  * Key1 - Number - has type for key
Function NewStructureObject()
	// empty
EndFunction
```

## Compliant Solution

```bsl

// Parameters:
//  Parameters - See NewStructureObject
Procedure Complaint(Parameters) Export
	// empty
EndProcedure

// Returns:
//  Parameters - Structure:
//  * Key1 - Number - has type for key
Function NewStructureObject()
	// empty
EndFunction
```

## See

