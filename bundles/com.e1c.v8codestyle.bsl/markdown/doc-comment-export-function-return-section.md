# Documentation comment return section for export function

## Noncompliant Code Example

```bsl

// Description without return section
Function NonComplaint() Export
	// empty
EndFunction

// See NonComplaint
Function NonComplaint2() Export
	// empty
EndFunction

```

## Compliant Solution

```bsl

// See Complaint2
Function Complaint() Export
	// empty
EndFunction

// Returns:
//  Structure - Parameters:
//  * Key1 - Number - has type for key
Function Complaint2() Export
	// empty
EndFunction

```

## See

