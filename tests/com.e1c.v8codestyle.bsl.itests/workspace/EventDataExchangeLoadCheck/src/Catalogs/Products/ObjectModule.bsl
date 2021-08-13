
Procedure BeforeDelete(Cancel)
	
	If Cancel Then
		return;
	EndIf;
	
EndProcedure

Procedure BeforeWrite(Cancel)
	
	If DataExchange.Load Then
		return;
	EndIf;
	
EndProcedure

Procedure OnWrite(Cancel)
	
	If Cancel Then
		return;
	EndIf;
	
	If DataExchange.Load Then
		// no return
	EndIf;
	
EndProcedure
