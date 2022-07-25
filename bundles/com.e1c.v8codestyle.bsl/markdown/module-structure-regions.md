# Module stucture

Method is outside a standard region

## Noncompliant Code Example

```bsl

Procedure Test()
//...
EndProcedure

```bsl

## Compliant Solution

```bsl

#Region Private

Procedure Test()
//...
EndProcedure

#EndRegion

```bsl

## See


- [Module structure](https://1c-dn.com/library/module_structure/)
- [Module structure](https://support.1ci.com/hc/en-us/articles/360011002360-Module-structure)
