# Documentation comment collection type definition has contain item type

## Noncompliant Code Example

```bsl
// Parameters:
//  Parameters - Array - here array without item type
Procedure NonComplaint(Parameters) Export
	// empty
EndProcedure

```

## Compliant Solution

```bsl
// Parameters:
//  Parameters - Array of Number - has type of collection item
Procedure Complaint(Parameters) Export
	// empty
EndProcedure

```

## See

