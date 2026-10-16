&AtClient
Procedure BeforeWrite(Cancel)
	ServerCallWithoutContext();
EndProcedure

&AtServerNoContext
Procedure ServerCallWithoutContext()
EndProcedure
