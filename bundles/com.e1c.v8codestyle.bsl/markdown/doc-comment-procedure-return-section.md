# Documentation comment has return section for procedure

## Noncompliant Code Example

```bsl

// Returns:
//  Structure - procedure should not have return section!
Procedure NonComplaint() Export
	// empty
EndProcedure

```

## Compliant Solution

```bsl

// Procedure description
Procedure Complaint() Export
	// empty
EndProcedure

```

## See

