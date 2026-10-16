&AtClient
Procedure OnActivateRow(Item, Row)
	AttachIdleHandler("IdleHandler", 1, False);
EndProcedure

&AtClient
Procedure IdleHandler()
EndProcedure
