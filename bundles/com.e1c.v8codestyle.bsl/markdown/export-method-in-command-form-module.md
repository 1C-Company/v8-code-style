# Restrictions on the use of export procedures and functions in a command and form modules

Do not place export procedures and functions in command and form modules.
To implement export procedures and functions, it is recommended that you use object modules, object manager modules, or 
common modules.

Recommended once a form is opened, don't address form methods and properties.

An exception to this rule are export procedures that handle notifications (NotifyDescription.ProcedureName).

## Noncompliant Code Example

```bsl
&AtClient
Procedure CommandProcessing(CommandParameter, CommandExecuteParameters) Export
EndProcedure
```

## Compliant Solution

```bsl
&AtClient
Procedure CommandProcessing(CommandParameter, CommandExecuteParameters)
EndProcedure
```

## See

- [Restrictions on the use of export procedures and functions](https://support.1ci.com/hc/en-us/articles/360011002940-Restrictions-on-the-use-of-export-procedures-and-functions)
- [Rules for creating form modules](https://support.1ci.com/hc/en-us/articles/360011003920-Rules-for-creating-form-modules)
- [Opening forms](https://support.1ci.com/hc/en-us/articles/360011003960-Opening-forms)
