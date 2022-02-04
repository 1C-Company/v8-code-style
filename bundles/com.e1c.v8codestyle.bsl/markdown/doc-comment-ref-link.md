# Documentation comment link referenced object exists

## Noncompliant Code Example

```bsl

// Parameters:
//  Parameters - See Complaint.UnknownParameter
Procedure NonComplaint(Parameters) Export
	// empty
EndProcedure

```

## Compliant Solution

```bsl

// See word - this "word" is not link in description
//
// Parameters:
//  Parameters - Here valid web-link See https://1c.ru
//  LinkToMethod - See NonComplaint()
Procedure Complaint(Parameters, LinkToMethod) Export
	// empty
EndProcedure

```

## See

