# Documentation comment field has no type definition

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

