
Procedure BeforeDelete(Cancel)
	
	If Cancel Then
		Return;
	EndIf;
	
EndProcedure

Procedure BeforeWrite(Cancel)
	
	If DataExchange.Load Then
		Return;
	EndIf;
	
EndProcedure

Procedure OnWrite(Cancel)
	
	If Cancel Then
		Return;
	EndIf;
	
	If DataExchange.Load Then
		// no return
	EndIf;
	
EndProcedure
