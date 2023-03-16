# Extension variable does not have extension prefix

All added objects (methods and objects, reports, processes and subsystems, and event handlers) of the extension, 
as well as the names of native methods and variables of extension modules, must have a prefix corresponding 
to the prefix of the extension itself.

## Noncompliant Code Example

```bsl
Var Perem Export;
```

## Compliant Solution

```bsl
Var Ext1_Perem Export;
```
## See
