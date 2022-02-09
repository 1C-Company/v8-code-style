
&AtClient
Procedure OnOpen(Cancel)
EndProcedure

&AtClient
Procedure OnClose(Exit)
	OnOpen(Exit);
EndProcedure

