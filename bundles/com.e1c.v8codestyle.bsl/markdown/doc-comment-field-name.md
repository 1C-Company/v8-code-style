# Documentation comment field is correct name

## Noncompliant Code Example

```bsl

// Parameters:
//  Parameters - Structure:
//  * 1Name - incorrect name
//  
Procedure NonComplaint(Parameters)
	
EndProcedure

```

## Compliant Solution

```bsl

// Parameters:
//  Parameters - Structure:
//  * Name - correct name
//  
Procedure Complaint(Parameters)
	
EndProcedure

```

## See

