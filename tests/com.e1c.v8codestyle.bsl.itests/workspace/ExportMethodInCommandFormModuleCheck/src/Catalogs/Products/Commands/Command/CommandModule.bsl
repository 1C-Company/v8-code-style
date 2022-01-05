
&AtClient
Procedure CommandProcessing(CommandParameter, CommandExecuteParameters)
	ShowQueryBox(New NotifyDescription("OfferDiscussionsCompletion", ThisObject), "", QuestionDialogMode.YesNo);
EndProcedure

&AtClient
Procedure OfferDiscussionsCompletion(Result, Param) Export
	//...
EndProcedure
