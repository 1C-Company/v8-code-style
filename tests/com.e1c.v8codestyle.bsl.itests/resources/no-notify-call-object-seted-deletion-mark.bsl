&AtClient
Procedure ClientProcedure()
	
	ServerProcedure();
	
EndProcedure

&AtServer
Procedure ServerProcedure()
	
	SetDeletionMark(True);
		
EndProcedure