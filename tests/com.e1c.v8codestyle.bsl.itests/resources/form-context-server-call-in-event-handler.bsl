&AtClient
Procedure BeforeWrite(Cancel)
	ServerCall();
EndProcedure

&AtClient
Procedure OnChange(Element)
	ServerCall();
EndProcedure

&AtServer
Procedure ServerCall()
EndProcedure
