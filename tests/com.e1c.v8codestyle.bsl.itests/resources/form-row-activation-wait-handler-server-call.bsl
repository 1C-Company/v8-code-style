&AtClient
Procedure OnActivateRow(Item, Row)
	AttachIdleHandler("IdleHandler", 1, True);
	ServerCall();
EndProcedure

&AtClient
Procedure IdleHandler()
EndProcedure

&AtServer
Procedure ServerCall()
EndProcedure
