# Documentation comment multi-line description ends on dot

## Noncompliant Code Example

```bsl
// First line
// second line
Procedure NonComplaint() Export
	// empty
EndProcedure

```

## Compliant Solution

```bsl
// First line
// second line.
Procedure Complaint() Export
	// empty
EndProcedure

```
## See

