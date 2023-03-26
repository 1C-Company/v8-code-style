
&AtServer
Procedure OnCreateAtServer(Cancel, StandardProcessing)
	
	P1 = Parameters.Parameter1;
	P2 = Parameters.Parameter2;
	P3 = Parameters.Parameter3;
	P4 = Parameters.Parameter4;
	If Parameters.Property("Parameter5") Then
		P5 = Parameters.Parameter5;
	EndIf;
	//@skip-check unknown-form-parameter-access
	P6 = Parameters.Parameter6;
	
EndProcedure