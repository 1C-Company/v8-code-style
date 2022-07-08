# Module stucture

Checks the standard interface regions for the existence of non-export methods
and the location of export methods outside the regions provided by the standard for export methods.

## Noncompliant Code Example

```bsl

#Region Public

Procedure Test()
      
EndProcedure

#EndRegion

```bsl

## Compliant Solution

```bsl

#Region Public

Procedure Test() Export
      
EndProcedure

#EndRegion

```bsl

## See


- [Module structure](https://1c-dn.com/library/module_structure/)
- [Module structure](https://support.1ci.com/hc/en-us/articles/360011002360-Module-structure)
