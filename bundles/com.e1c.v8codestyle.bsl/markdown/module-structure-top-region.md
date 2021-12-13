# Module stucture top region

Check that module structure region is on top

## Noncompliant Code Example

```bsl

#Region NotEmpty

#Region Public

Procedure Test() Export
	
EndProcedure

#EndRegion

#EndRegion

```bsl

## Compliant Solution

```bsl

#Region Public

Procedure Test1() Export
	
EndProcedure

#EndRegion

```bsl

## See


- [Module structure](https://1c-dn.com/library/module_structure/)
- [Module structure](https://support.1ci.com/hc/en-us/articles/360011002360-Module-structure)
