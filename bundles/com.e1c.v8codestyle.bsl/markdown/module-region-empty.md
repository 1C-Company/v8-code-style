# Region is empty

Check that module region is empty

## Noncompliant Code Example

```bsl

#Region Empty

#EndRegion

```bsl


## Compliant Solution

```bsl

#Region NotEmpty

Procedure Test()
	
EndProcedure

#EndRegion

```bsl

## See

- [Module structure](https://1c-dn.com/library/module_structure/)
- [Module structure](https://support.1ci.com/hc/en-us/articles/360011002360-Module-structure)
