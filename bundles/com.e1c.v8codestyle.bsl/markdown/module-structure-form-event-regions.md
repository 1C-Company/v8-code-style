# Checks the region of event handlers for methods related only to handlers

The Form event handlers section contains the procedures that are event handlers of the form: 
OnCreateAtServer, OnOpen, and so on.
The Form header items event handlers section contains the procedures that are handlers of the items located within 
the main form area (this includes everything that does not belong to the tables within the form).
The Form table event handlers of <table name> table sections contain the procedures that are handlers 
of form tables and table items. An individual section is created for procedures that handle each table.
The Form command handlers section contains procedures that handle form commands 
(the procedure name is specified in the Action property of the command).

## Noncompliant Code Example

```bsl

#Region FormEventHandlers

Procedure WrongMethod()
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
