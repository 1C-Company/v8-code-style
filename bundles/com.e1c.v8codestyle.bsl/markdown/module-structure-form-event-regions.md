# Module stucture

Checks the region of event handlers for methods related only to handlers

## Noncompliant Code Example

```bsl

#Region FormEventHandlers

Procedure Test()
    //TODO: Insert the handler content
EndProcedure

#EndRegion

```bsl

## Compliant Solution

```bsl

#Region FormEventHandlers

&AtClient
Procedure OnOpen(Cancel)
    //TODO: Insert the handler content
EndProcedure

#EndRegion

```bsl

## See


- [Module structure](https://1c-dn.com/library/module_structure/)
- [Module structure](https://support.1ci.com/hc/en-us/articles/360011002360-Module-structure)
