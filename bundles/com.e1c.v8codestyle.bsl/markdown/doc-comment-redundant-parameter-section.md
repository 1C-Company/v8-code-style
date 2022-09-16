# Documentation comment parameter section is redundant

Documentation comment for method without parameters should not have parameter section and should be removed.

## Noncompliant Code Example

```bsl
// Parameters:
//  Parameters - Method should not have parameter section
Procedure NonComplaint()
	// empty
EndProcedure

```

## Compliant Solution

```bsl
// Method without parameters should not have such section
Procedure Complaint()
	// empty
EndProcedure

```

## See

