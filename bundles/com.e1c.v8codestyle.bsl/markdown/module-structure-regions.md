# Module stucture

Check that module structure regions is fitted

## Noncompliant Code Example

```bsl

#Region Internal
// Enter code here.
#EndRegion

#Region Private
// Enter code here.
#EndRegion

```bsl

## Compliant Solution

```bsl

#Region Public
// Enter code here.
#EndRegion

#Region Internal
// Enter code here.
#EndRegion

#Region Private
// Enter code here.
#EndRegion

```bsl

## See


- [Module structure](https://1c-dn.com/library/module_structure/)
- [Module structure](https://support.1ci.com/hc/en-us/articles/360011002360-Module-structure)
