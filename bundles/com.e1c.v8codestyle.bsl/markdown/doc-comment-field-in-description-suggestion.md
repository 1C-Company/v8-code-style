# Documentation comment multi-line description contains field definition

## Noncompliant Code Example

```bsl

// Parameters
//  Parameters - Structure - description
//  * Key1 - this field is part of description
Procedure NonComplaint(Parameters) Export
	// empty
EndProcedure

```

## Compliant Solution

```bsl

// Parameters:
//  Parameters - Structure - description:
//  * Key1 - Number - this field is extension of the structure
Procedure Complaint(Parameters) Export
	// empty
EndProcedure

```

## See

