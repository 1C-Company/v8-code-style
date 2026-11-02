&AtClient
Procedure ClientProcedure()
	
	ServerProcedure();
	
	Notify("EventName", "SomeData", ThisObject);
		
EndProcedure

&AtServer
Procedure ServerProcedure()
	
	Write();	
		
EndProcedure