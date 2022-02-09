# Documentation comment multi-line description contains parameter definition

## Noncompliant Code Example

```bsl
// Description
// 
// Parameters
//  Parameter1 - this parameter is part of description
Procedure NonComplaint(Parameter1) Export
	// empty
EndProcedure

```

## Compliant Solution

```bsl
// Description
// 
// Parameters:
//  Parameter1 - this parameter is the section of parameters
Procedure Complaint(Parameters) Export
	// empty
EndProcedure

```

## See

