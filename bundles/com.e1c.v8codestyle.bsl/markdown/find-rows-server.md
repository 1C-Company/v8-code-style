# Check the FindRows function is called only on the server

Call the FindRows function only on the server.

## Noncompliant Code Example

&AtClient
 Procedure CheckForRowsWithEmptyQuantity()
 If Object.Goods.FindRows(New Structure("Count", 0 )).Count() > 0 Then
 DoMessageBox(NStr("en = 'There are rows with zero quantity'"));
 EndIf;
 EndProcedure
 
## Compliant Solution

&AtServer
 Function HasRowsWithZeroQuantity()
 Return Object.Goods.FindRows(New Structure("Count", 0 )).Count() > 0;
EndFunction

&AtClient
 Procedure CheckForRowsWithEmptyQuantity()
 If HasRowsWithZeroQuantity() Then
 DoMessageBox(NStr("en = 'There are rows with zero quantity'"));
 EndIf;
 EndProcedure

## See
[FormDataCollection object](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/Designing_user_interfaces/Implementation_of_forms/FormDataCollection_object/?language=en)