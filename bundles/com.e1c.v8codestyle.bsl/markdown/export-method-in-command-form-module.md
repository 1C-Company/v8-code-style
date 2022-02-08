# Restrictions on the use of export procedures and functions in a command and form modules

Do not embed export procedures and functions in modules of commands and forms. 
You cannot address such modules from external code, so embedded export procedures and functions become dysfunctional.

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
