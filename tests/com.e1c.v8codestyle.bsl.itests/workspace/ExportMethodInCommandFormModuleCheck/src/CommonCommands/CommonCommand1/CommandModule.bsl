
&AtClient
Procedure CommandProcessing(CommandParameter, CommandExecuteParameters)
	NotifyDescription = New NotifyDescription("OfferDiscussionsCompletion", ThisObject);
	ShowQueryBox(NotifyDescription, "", QuestionDialogMode.YesNo);
EndProcedure

&AtClient
Procedure OfferDiscussionsCompletion(Result, Param) Export
	//...
EndProcedure

