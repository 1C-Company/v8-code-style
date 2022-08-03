# The Event handlers section contains the event handlers of the object module (OnWrite, Posting, and so on)

Checks the region of event handlers for methods related only to handlers

## Noncompliant Code Example

```bsl

#Region EventHandlers

Procedure Test()
    //TODO: Insert the handler content
EndProcedure

#EndRegion

```bsl

## Compliant Solution

```bsl

#Region EventHandlers

Procedure FormGetProcessing(FormType, Parameters, SelectedForm, AdditionalInformation, StandardProcessing)
    //TODO: Insert the handler content
EndProcedure

#EndRegion

```bsl

## See


- [Module structure](https://1c-dn.com/library/module_structure/)
- [Module structure](https://support.1ci.com/hc/en-us/articles/360011002360-Module-structure)
