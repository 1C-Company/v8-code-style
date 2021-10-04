// @strict-types

// Parameters:
//  Parameters - String
Procedure NonComplaint(Parameters) Export
	Parameters = 1;
EndProcedure

Procedure Complaint() Export
	Parameters = True;
	If Parameters = "" Then
		Parameters = UndefinedFunction(); // Boolean
	EndIf;
EndProcedure

Function UndefinedFunction()
	Return "";
EndFunction
