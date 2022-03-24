# Documentation comment return section contains valid types

## Noncompliant Code Example

```bsl

// Returns:
//  Empty types here
Function NonComplaint(Parameters) Export
	// empty
EndFunction

// Here ref link to function without return type
//
// Returns:
//  See NonComplaint()
Function NonComplaint2(Parameters) Export
	// empty
EndFunction

// Returns:
//  UnknownType - here unknown return type
Function NonComplaint3(Parameters) Export
	// empty
EndFunction

```

## Compliant Solution

```bsl

// Parameters:
//  See Complaint2()
Function Complaint(Parameters) Export
	// empty
EndFunction

// Parameters:
//  Structure - has return type
Function Complaint2(Parameters) Export
	// empty
EndFunction

```

## See

