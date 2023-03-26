# Configuration module texts should not contain unused export procedures and functions

Make sure your configuration does not contain unused metadata objects, such as catalogs, documents, 
command interface sections, and other. Also it must not contain the code of common modules, procedures, 
functions, and variables, which is not used in the configuration and for integration with other systems.

The check searches for all references to the method, so it can take a long time.

## Noncompliant Code Example

```bsl
Procedure Processing() Export
EndProcedure
```

## Compliant Solution

```bsl
#Region Public
Procedure Processing() Export
EndProcedure
#EndRegion
```

## See

- [General configuration requirements](https://support.1ci.com/hc/en-us/articles/360011107839-General-configuration-requirements)
