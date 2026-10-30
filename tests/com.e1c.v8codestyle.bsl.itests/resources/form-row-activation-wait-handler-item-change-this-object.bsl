&AtClient
Procedure OnActivateRow(Item, Row)
	AttachIdleHandler("IdleHandler", 1, True);
	ThisObject.Items.Table.Visible = False;
EndProcedure

&AtClient
Procedure IdleHandler()
	ServerCall();
EndProcedure

&AtServer
Procedure ServerCall()
EndProcedure
