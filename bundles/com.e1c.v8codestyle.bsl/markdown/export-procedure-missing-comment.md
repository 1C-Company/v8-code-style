# Export procedure (function) should be described by adding comment

Procedures and functions that belong to **program interface of modules** are required to have comments.
Such procedures and functions are intended to be used in other functional subsystems (or in other applications)
that might be in the scope of responsibility of other developers, and they should, therefore, be properly documented.

## Noncompliant Code Example

```bsl

#Region Public

Function RolesAvailable(RoleNames) Export 
  // code here
EndFunction

#EndRegion

```

## Compliant Solution

```bsl

#Region Public

// Defines the availability of RoleNames roles to the current user,
// as well as the availability of administrator rights. 
//
// Parameters:
// RoleNames - String - comma-separated names of roles whose availability is checked.
//
// Returns:
// Boolean - True if at least one of the passed roles is available to the current user or the
//           current user has administrative rights.
//
// Example:
// If RolesAvailable("UseReportMailingLists,SendMail") Then ...
//
Function RolesAvailable(RoleNames) Export
  // code here
EndFunction

#EndRegion
 
```

## See

- [Procedure and function descriptions](https://1c-dn.com/library/procedure_and_function_descriptions/)
