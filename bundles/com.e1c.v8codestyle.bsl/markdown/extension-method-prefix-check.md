# Extension method does not have extension prefix

All added objects (methods and objects, reports, processes and subsystems, and event handlers) of the extension, 
as well as the names of native methods and variables of extension modules, must have a prefix corresponding 
to the prefix of the extension itself.

## Noncompliant Code Example

```bsl
&Before("NonComplient")
Procedure Ext_NonComplient()
    //TODO: Insert the handler content
EndProcedure
```

## Compliant Solution

```bsl
&After("Complient")
Procedure Ext1_Complient()
    //TODO: Insert the handler content
EndProcedure
```
## See

