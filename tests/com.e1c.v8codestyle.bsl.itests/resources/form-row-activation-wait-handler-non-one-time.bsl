&AtClient
Procedure OnActivateRow(Item, Row)
	AttachIdleHandler("IdleHandler", 1);
EndProcedure

&AtClient
Procedure IdleHandler()
	ServerCall();
EndProcedure

&AtServer
Procedure ServerCall()
EndProcedure
