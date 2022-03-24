# Documentation comment type definition

## Noncompliant Code Example

```bsl
// Parameters:
//  Parameters - Structure1 - incorrect type
Procedure NonComplaint(Parameters) Export
	// empty
EndProcedure

```

## Compliant Solution

```bsl
// Parameters:
//  Parameters - Structure - correct type
Procedure Complaint(Parameters) Export
	// empty
EndProcedure

```

## See

