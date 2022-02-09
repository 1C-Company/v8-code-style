# Documentation comment Field has no type definition

Check of module strict types system that documentation comment field has section with types

## Noncompliant Code Example

```bsl
// Parameters:
//  Parameters - Structure:
//  * Key1 - has no type for key
Procedure NonComplaint(Parameters) Export
	// empty
EndProcedure

```

## Compliant Solution

```bsl

// Parameters:
//  Parameters - Structure:
//  * Key1 - Number - has type for key
//  * Key2 - See NonComplaint.Parameters
Procedure Complaint(Parameters) Export
	// empty
EndProcedure

```

## See

