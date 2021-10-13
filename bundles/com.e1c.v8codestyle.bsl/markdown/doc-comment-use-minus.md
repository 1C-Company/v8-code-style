# Use only hyphen-minus in documentation comment

In Description of Documentation comment model should use only hyphen-minus symbol instead of usual hyphen or different dashes.

This check analyze wrong "minus" only in first text part of description which goes after field declaration to catch possible wrong parsing of the documentation comment model.

## Noncompliant Code Example

```bsl
// Parameters:
//  Parameters – Structure - first is middle-dash and second is minus:
//  * Key1 - Number ⸺ incorrect long dash
Procedure NonComplaint(Parameters) Export
	// empty
EndProcedure
```


## Compliant Solution


```bsl
// Parameters:
//  Parameters - Structure - both are minus:
//  * Key1 - Number - used correct minus
Procedure Complaint(Parameters) Export
	// empty
EndProcedure
```

## See


- [Wikipedia: Hyphen](https://en.wikipedia.org/wiki/Hyphen)
- [Wikipedia: Dash](https://en.wikipedia.org/wiki/Dash)
