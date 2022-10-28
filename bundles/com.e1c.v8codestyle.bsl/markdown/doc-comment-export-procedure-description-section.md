# Documentation comment has "Description" section for export procedure or function

In the **Description section**, explain the purpose of the procedure or function in a short, but clear way,
so that the reader would understand its behavior without looking into the source code.
Here, you can also include the description of its mechanics and links to related procedures and functions.

If the procedure or function has no parameters, this can be the only comment section.
The description text must not be identical to the name of the function or procedure.
Start the description with a verb. For functions, usually, the description starts with "Returns."
If returning a result is not the main purpose of the function,
start the section with a verb that describes the main purpose.
For example, "Validates," "Compares," or "Calculates."
Don't start the description with excessive words like "Procedure" or its name.

## Noncompliant Code Example

```bsl

#Region Public

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

- [Procedure and function descriptions](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/Code_conventions/Module_formatting/Procedure_and_function_description)
