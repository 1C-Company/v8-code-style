
&AtServer
Procedure OnCreateAtServer(CancelRenamed, StandardProcessing)
	
	// Noncompliant
	CancelRenamed = False;
	StandardProcessing = True;

EndProcedure

&AtClient
Procedure DescriptionStartListChoice(Item, StandardProcessingRenamed)
	
	// Noncompliant
	StandardProcessingRenamed = True;
	
EndProcedure

&AtClient
Procedure BeforeClose(Cancel, Exit, WarningText, StandardProcessingRenamed)
	
	// Compliant
	Cancel = True;
	StandardProcessingRenamed = False;

EndProcedure

&AtClient
Procedure DescriptionAutoComplete(Item, Text, ChoiceData, DataGetParameters, Waiting, StandardProcessingRenamed)
	
	// Compliant
	StandardProcessingRenamed = False;
	
EndProcedure
